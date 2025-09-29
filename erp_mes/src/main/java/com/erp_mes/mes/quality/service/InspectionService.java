package com.erp_mes.mes.quality.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.service.CommonCodeService;
import com.erp_mes.erp.config.util.SessionUtil;
import com.erp_mes.mes.lot.trace.TrackLot;
import com.erp_mes.mes.plant.dto.ProcessDTO;
import com.erp_mes.mes.plant.service.ProcessService;
import com.erp_mes.mes.pm.mapper.WorkOrderMapper;
import com.erp_mes.mes.pop.dto.DefectDTO;
import com.erp_mes.mes.quality.dto.InspectionDTO;
import com.erp_mes.mes.quality.dto.InspectionFMDTO;
import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import com.erp_mes.mes.quality.dto.InspectionRegistrationRequestDTO;
import com.erp_mes.mes.quality.dto.InspectionResultDTO;
import com.erp_mes.mes.quality.dto.InspectionResultDataDTO;
import com.erp_mes.mes.quality.dto.InspectionTargetDTO;
import com.erp_mes.mes.quality.entity.InspectionFM;
import com.erp_mes.mes.quality.mapper.QualityMapper;
import com.erp_mes.mes.quality.repository.InspectionFMRepository;
import com.erp_mes.mes.stock.dto.MaterialDTO;
import com.erp_mes.mes.stock.service.StockService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class InspectionService {

    private final QualityMapper qualityMapper;
    private final InspectionFMRepository inspectionFMRepository;
    private final WorkOrderMapper workOrderMapper;
    private final ProcessService processService;
    private final StockService stockService;
    private final CommonCodeService commonCodeService;

    @Transactional(readOnly = true)
    public List<InspectionFMDTO> findAllInspectionFMs() {
        return inspectionFMRepository.findAll().stream()
                .map(entity -> {
                    InspectionFMDTO dto = new InspectionFMDTO();
                    dto.setInspectionFMId(entity.getInspectionFMId());
                    dto.setInspectionType(entity.getInspectionType());
                    dto.setItemName(entity.getItemName());
                    dto.setMethodName(entity.getMethodName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InspectionItemDTO> getInspectionItems() {
        List<InspectionItemDTO> items = qualityMapper.findAllItems();

        Map<Long, String> processMap = processService.getProcessList().stream()
            .filter(dto -> dto.getProId() != null && dto.getProNm() != null)
            .collect(Collectors.toMap(ProcessDTO::getProId, ProcessDTO::getProNm));

        Map<String, String> materialMap = stockService.getMaterialList().stream()
            .filter(dto -> dto.getMaterialId() != null && dto.getMaterialName() != null)
            .collect(Collectors.toMap(MaterialDTO::getMaterialId, MaterialDTO::getMaterialName));

        items.forEach(item -> {
            if (item.getProId() != null && processMap.containsKey(item.getProId())) {
                item.setProNm(processMap.get(item.getProId()));
            }
            if (item.getMaterialId() != null && materialMap.containsKey(item.getMaterialId())) {
                item.setMaterialName(materialMap.get(item.getMaterialId()));
            }
        });

        return items;
    }

    @Transactional
    public void registerInspectionRecord(InspectionFMDTO inspectionFMDTO) {
        InspectionFM inspectionFM = InspectionFM.builder()
                .inspectionType(inspectionFMDTO.getInspectionType())
                .itemName(inspectionFMDTO.getItemName())
                .methodName(inspectionFMDTO.getMethodName())
                .build();
        inspectionFMRepository.save(inspectionFM);
    }

    @Transactional
    public void registerInspectionItem(InspectionItemDTO inspectionItemDTO) {
        qualityMapper.insertItem(inspectionItemDTO);
    }

    public int updateInspectionFm(InspectionFMDTO inspectionFMDTO) {
        return qualityMapper.updateInspectionFm(inspectionFMDTO);
    }

    public int updateInspectionItem(InspectionItemDTO inspectionItemDTO) {
        return qualityMapper.updateInspectionItem(inspectionItemDTO);
    }

    @Transactional
    public void deleteInspectionRecords(List<Long> inspectionFMIds) {
        inspectionFMRepository.deleteAllByIdInBatch(inspectionFMIds);
    }

    @Transactional
    public void deleteInspectionItems(List<Long> itemIds) {
        qualityMapper.deleteItems(itemIds);
    }

    public List<InspectionResultDTO> getInspectionResultList() {
        return qualityMapper.getInspectionResultList();
    }
    
    @Transactional
    public void verifyIncomingCount(String inId, Long acceptedCount, Long defectiveCount, String empId, String lotId, String inspectionType, String defectType, String remarks, String materialId) {
        // 1. INPUT 테이블에서 기존 in_count를 조회
    	Integer expectedCount = qualityMapper.findInCountByInId(inId);
        
        if (expectedCount == null) {
            throw new IllegalArgumentException("입고 항목을 찾을 수 없습니다.");
        }
        
        // 2. 검사 결과 판정
        boolean isCountMatch = (expectedCount.equals(acceptedCount.intValue() + defectiveCount.intValue()));
        String inspectionResult = isCountMatch ? "합격" : "불합격";
        String inspectionRemarks = isCountMatch ? "합격: " + acceptedCount + "개, 불량: " + defectiveCount + "개" : "수량 불일치";
        
        // 3. INSPECTION 및 INSPECTION_RESULT 테이블에 검사 이력 등록
        InspectionDTO inspectionDTO = new InspectionDTO();
        inspectionDTO.setInspectionType(inspectionType);
        inspectionDTO.setEmpId(empId);
        inspectionDTO.setLotId(lotId);
        inspectionDTO.setMaterialId(materialId);
        qualityMapper.insertInspection(inspectionDTO);
        Long newInspectionId = inspectionDTO.getInspectionId();
        
        InspectionResultDTO resultDTO = new InspectionResultDTO();
        resultDTO.setInspectionId(newInspectionId);
        resultDTO.setInspectionType(inspectionType);
        resultDTO.setResult(inspectionResult);
        resultDTO.setRemarks(inspectionRemarks);
        qualityMapper.insertInspectionResult(resultDTO);
        
        if (defectiveCount > 0) {
            DefectDTO defectDTO = new DefectDTO();
            String finalDefectType = (defectType != null) ? defectType : "DEFECT"; 
            String finalRemarks = (remarks != null) ? remarks : "상세 사유 없음";
            defectDTO.setDefectType(finalDefectType); // 불량사유
            defectDTO.setDefectReason(finalRemarks);
            defectDTO.setDefectQty(defectiveCount);
            defectDTO.setProductNm(qualityMapper.findTargetNameByInId(inId)); // 자재명 조회
            defectDTO.setEmpId(empId);
            defectDTO.setDefectLocation(2L); // 2:QC/QA팀
            defectDTO.setLotId(lotId);
            
            qualityMapper.insertDefectItem(defectDTO);
        }
        
        // 4. INPUT 테이블의 상태 업데이트
        if (acceptedCount > 0) {
            qualityMapper.updateInputStatusByInId(inId, "입고완료", acceptedCount);
        } else {
            qualityMapper.updateInputStatusByInId(inId, "불량", 0L); // 불량만 있으면 '불량' 상태로 변경
        }
    }
    
    @Transactional(readOnly = true)
    public List<InspectionTargetDTO> getIncomingInspectionTargets() {
        List<InspectionTargetDTO> targets = qualityMapper.getIncomingInspectionTargets();
        Map<String, String> qcTypeMap = getQcTypeMap();
        targets.forEach(target -> target.setInspectionTypeName(qcTypeMap.get(target.getInspectionType())));
        return targets;
    }

    // WORK_ORDER_ID별로 그룹화된 데이터를 가져오는 메서드
    @Transactional(readOnly = true)
    public List<InspectionTargetDTO> getProcessInspectionTargetsGrouped() {
        return qualityMapper.getProcessInspectionTargetsGrouped();
    }
    
    // 특정 WORK_ORDER의 공정 상세 이력을 가져오는 메서드
    @Transactional(readOnly = true)
    public List<InspectionTargetDTO> getProcessDetails(String workOrderId) {
        List<InspectionTargetDTO> details = qualityMapper.getProcessDetails(workOrderId);
        
        // 공통 코드 이름 매핑
        Map<String, String> qcTypeMap = commonCodeService.findByComId("QC").stream()
            .collect(Collectors.toMap(CommonDetailCode::getComDtId, CommonDetailCode::getComDtNm));
            
        details.forEach(target -> target.setInspectionTypeName(qcTypeMap.get(target.getInspectionType())));
        
        return details;
    }

    private Map<String, String> getQcTypeMap() {
        List<CommonDetailCode> qcTypes = commonCodeService.findByComId("QC");
        return qcTypes.stream()
                .collect(Collectors.toMap(CommonDetailCode::getComDtId, CommonDetailCode::getComDtNm));
    }

    @Transactional(readOnly = true)
    public List<InspectionItemDTO> getInspectionItemByMaterialId(String materialId) {
        return qualityMapper.findInspectionItemsByMaterialId(materialId);
    }

    @Transactional(readOnly = true)
    public List<InspectionItemDTO> findInspectionItemsByProcessIdAndSeq(Long processId, String proSeq) {
        return qualityMapper.findInspectionItemsByProcessIdAndSeq(processId, proSeq);
    }
    
    @Transactional
    public void registerInspection(InspectionRegistrationRequestDTO requestDTO) {
        // 1. INSPECTION 테이블에 데이터 삽입
        InspectionDTO inspectionDTO = new InspectionDTO();
        inspectionDTO.setInspectionType(requestDTO.getInspectionType());
        inspectionDTO.setEmpId(requestDTO.getEmpId());
        inspectionDTO.setLotId(requestDTO.getLotId());

        // 검사 출처(targetSource)에 따라 다른 정보 설정
        if ("WorkOrder".equals(requestDTO.getTargetSource())) {
            inspectionDTO.setProductId(requestDTO.getProductId());
            inspectionDTO.setProcessId(requestDTO.getProcessId());
            inspectionDTO.setMaterialId(null);
        } else if ("Receiving".equals(requestDTO.getTargetSource())) {
            inspectionDTO.setMaterialId(requestDTO.getMaterialId());
            inspectionDTO.setProductId(null);
            inspectionDTO.setProcessId(null);
        }
        
        qualityMapper.insertInspection(inspectionDTO);
        Long newInspectionId = inspectionDTO.getInspectionId();

        // 2. INSPECTION_RESULT 테이블에 데이터 삽입
        for (InspectionResultDataDTO resultData : requestDTO.getInspectionResults()) {
            InspectionResultDTO resultDTO = new InspectionResultDTO();
            resultDTO.setInspectionId(newInspectionId);
            resultDTO.setInspectionType(requestDTO.getInspectionType());
            resultDTO.setResult(resultData.getResult());
            
            // 비고 처리 로직 수정
            String remarks = (resultData.getRemarks() != null) ? resultData.getRemarks() : "";
            resultDTO.setRemarks(remarks);
            
            qualityMapper.insertInspectionResult(resultDTO);
        }
        
        // 3. 원본 테이블 상태 업데이트
        if ("WorkOrder".equals(requestDTO.getTargetSource())) {
            // 공정 검사 완료 후 작업지시 상태 업데이트
            qualityMapper.updateWorkOrderStatus(requestDTO.getTargetId());
        }
    }
    
    // 공정 검사 수량 등록 및 처리
    @Transactional
    @TrackLot(tableName = "inspection", pkColumnName = "inspection_id") //lot 생성
    public void registerProcessInspectionResult(InspectionRegistrationRequestDTO requestDTO) {
        
        String workOrderIdString = requestDTO.getTargetId(); // 클라이언트에서 받은 String WORK_ORDER_ID

        // WORK_ORDER_ID를 Long 타입으로 변환
        Long workOrderIdLong;
        try {
            workOrderIdLong = Long.parseLong(workOrderIdString);
        } catch (NumberFormatException e) {
            // 숫자가 아닐 경우 (예: IN_ID가 잘못 들어온 경우) 예외 처리
        	 log.error("WORK_ORDER_ID 변환 실패: {}", workOrderIdString, e);
            throw new IllegalArgumentException("WORK_ORDER_ID 값이 올바른 숫자 형식이 아닙니다: " + workOrderIdString);
        }
        
        // 이 시점부터 workOrderIdString은 String이 필요한 매퍼 메서드에,
        // workOrderIdLong은 Long이 필요한 DTO 필드/매퍼 메서드에 사용합니다.

        Long acceptedCount = requestDTO.getAcceptedCount();
        Long defectiveCount = requestDTO.getDefectiveCount();
        String empId = requestDTO.getEmpId();
        String inspectionType = requestDTO.getInspectionType();
        String lotId = requestDTO.getLotId();
        String remarks = requestDTO.getRemarks();
        String defectType = requestDTO.getDefectType();
        
        // 1. 검사 결과 판정
        Long totalCount = acceptedCount + defectiveCount;
        String inspectionRemarks = "합격: " + acceptedCount + "개, 불량: " + defectiveCount + "개";
        String inspectionResult = (defectiveCount > 0 && defectiveCount.equals(totalCount)) ? "불합격" : "합격";
        
        // 2. INSPECTION 테이블에 검사 이력 등록 (Long 타입이 필요하지 않으므로 기존 DTO 유지)
        InspectionDTO inspectionDTO = new InspectionDTO();
        inspectionDTO.setInspectionType(inspectionType);
        inspectionDTO.setEmpId(empId);
        inspectionDTO.setProductId(requestDTO.getProductId());
        inspectionDTO.setProcessId(requestDTO.getProcessId());
        inspectionDTO.setLotId(lotId);
        
        qualityMapper.insertInspection(inspectionDTO);
        Long newInspectionId = inspectionDTO.getInspectionId();
        
        // 3. INSPECTION_RESULT 테이블에 검사 결과 등록
        InspectionResultDTO resultDTO = new InspectionResultDTO();
        resultDTO.setInspectionId(newInspectionId);
        resultDTO.setInspectionType(inspectionType);
        resultDTO.setResult(inspectionResult);
        resultDTO.setRemarks(inspectionRemarks);
        qualityMapper.insertInspectionResult(resultDTO);
        
        // 4. 불량 등록 (defectiveCount > 0 인 경우)
        if (defectiveCount > 0) {
            DefectDTO defectDTO = new DefectDTO();
            
            String productName = qualityMapper.findTargetNameByWorkOrderId(workOrderIdString);

            defectDTO.setDefectType(defectType != null ? defectType : "DEFECT"); 
            defectDTO.setDefectReason(remarks != null ? remarks : "상세 사유 없음");
            defectDTO.setDefectQty(defectiveCount);
            defectDTO.setProductNm(productName); 
            defectDTO.setEmpId(empId);
            defectDTO.setWorkOrderId(workOrderIdLong); 
            defectDTO.setLotId(requestDTO.getLotId()); 
            defectDTO.setDefectLocation(2L); 
            
            qualityMapper.insertDefectItem(defectDTO);
        }

        // 5. WORK_RESULT 업데이트 및 WORK_ORDER 상태 업데이트
        qualityMapper.updateWorkOrderStatus(workOrderIdString); 
        
     // finish lot 생성
        if (newInspectionId != null) {
        	HttpSession session = SessionUtil.getSession();
            session.setAttribute("targetIdValue", newInspectionId);	
		}
    }

    // 검사 ID로 상세 정보를 조회하는 메서드
    @Transactional(readOnly = true)
    public InspectionTargetDTO getInspectionDetail(Long inspectionId) {
        
        String inspectionType = qualityMapper.findInspectionTypeById(inspectionId); 
        InspectionTargetDTO detail = null;

        if (inspectionType == null) {
            return null; // 검사 기록이 없음
        }

        if ("QC001".equals(inspectionType)) {
            // 수입 검사 (단일 행이 기대됨)
            detail = qualityMapper.getIncomingDetail(inspectionId);
        } else if ("QC002".equals(inspectionType) || "QC003".equals(inspectionType)) {
            // 1. Mapper에서 List로 결과를 받습니다. (Mapper 인터페이스 변경 필요)
            List<InspectionTargetDTO> details = qualityMapper.getProcessDetail(inspectionId); 
            
            // 2. 결과가 있다면 첫 번째 요소만 선택합니다.
            if (details != null && !details.isEmpty()) {
                detail = details.get(0);
            }
        } 
        // 검사 유형 이름을 매핑
        if (detail != null) {
            Map<String, String> qcTypeMap = getQcTypeMap();
            detail.setInspectionTypeName(qcTypeMap.get(detail.getInspectionType()));
        }

        return detail;
    }
    
}
