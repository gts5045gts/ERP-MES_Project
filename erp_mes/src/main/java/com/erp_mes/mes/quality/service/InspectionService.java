//package com.erp_mes.mes.quality.service;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.erp_mes.mes.plant.dto.ProcessDTO;
//import com.erp_mes.mes.plant.service.ProcessService;
//import com.erp_mes.mes.pm.dto.WorkOrderDTO;
//import com.erp_mes.mes.quality.dto.InspectionDTO;
//import com.erp_mes.mes.quality.dto.InspectionFMDTO;
//import com.erp_mes.mes.quality.dto.InspectionItemDTO;
//import com.erp_mes.mes.quality.dto.InspectionResultDTO;
//import com.erp_mes.mes.quality.entity.InspectionFM;
//import com.erp_mes.mes.quality.mapper.QualityMapper;
//import com.erp_mes.mes.quality.repository.InspectionFMRepository;
//import com.erp_mes.mes.stock.dto.MaterialDTO;
//import com.erp_mes.mes.stock.service.StockService;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//
//@Log4j2
//@Service
//@RequiredArgsConstructor
//public class InspectionService {
//
//	private final QualityMapper qualityMapper;
//	private final InspectionFMRepository inspectionFMRepository;
////	private final WorkOrderMapper workOrderMapper;
//	private final ProcessService processService;
//	private final StockService stockService;
//	
//    @Transactional(readOnly = true)
//    public List<InspectionFMDTO> findAllInspectionFMs() {
//        return inspectionFMRepository.findAll().stream()
//                .map(entity -> {
//                    InspectionFMDTO dto = new InspectionFMDTO();
//                    dto.setInspectionFMId(entity.getInspectionFMId());
//                    dto.setInspectionType(entity.getInspectionType());
//                    dto.setItemName(entity.getItemName());
//                    dto.setMethodName(entity.getMethodName());
//                    return dto;
//                })
//                .collect(Collectors.toList());
//    }
//
//    // 오른쪽 테이블 (검사 항목별 허용 공차) 조회 로직
//    @Transactional(readOnly = true)
//    public List<InspectionItemDTO> getInspectionItems() {
//        // 1. MyBatis 매퍼를 통해 새로운 테이블 구조의 InspectionItem 데이터 가져오기
//        //    이 쿼리는 materialId와 proId를 조회해야 합니다.
//        List<InspectionItemDTO> items = qualityMapper.findAllItems();
//
//        // 2. Process와 Material 데이터를 조회하여 맵 생성 (성능 최적화)
//        Map<Long, String> processMap = processService.getProcessList().stream()
//                .collect(Collectors.toMap(ProcessDTO::getProId, ProcessDTO::getProNm));
//
//        Map<String, String> materialMap = stockService.getMaterialList().stream()
//                .collect(Collectors.toMap(MaterialDTO::getMaterialId, MaterialDTO::getMaterialName));
//
//        // 3. 데이터 가공: DTO에 이름 정보 설정
//        items.forEach(item -> {
//            // proId를 이용해 proNm을 DTO에 설정
//            if (item.getProId() != null) {
//                item.setProNm(processMap.get(item.getProId()));
//            }
//            // materialId를 이용해 materialName을 DTO에 설정
//            if (item.getMaterialId() != null) {
//                item.setMaterialName(materialMap.get(item.getMaterialId()));
//            }
//        });
//
//        return items;
//    }
//    
//    // 왼쪽 테이블 (검사 유형별 기준) 등록 로직
//    @Transactional
//    public void registerInspectionRecord(InspectionFMDTO inspectionFMDTO) {
//        InspectionFM inspectionFM = InspectionFM.builder()
//                .inspectionType(inspectionFMDTO.getInspectionType())
//                .itemName(inspectionFMDTO.getItemName())
//                .methodName(inspectionFMDTO.getMethodName())
//                .build();
//        inspectionFMRepository.save(inspectionFM);
//    }
//    
//    // 오른쪽 테이블 (검사 항목별 허용 공차) 등록 로직
//    @Transactional
//    public void registerInspectionItem(InspectionItemDTO inspectionItemDTO) {
//        // QualityMapper의 insertItem() 메서드를 호출하여 데이터를 삽입
//        qualityMapper.insertItem(inspectionItemDTO);
//    }
//    
//    // 검사기준삭제
//    @Transactional
//    public void deleteInspectionRecords(List<Long> inspectionFMIds) {
//        inspectionFMRepository.deleteAllByIdInBatch(inspectionFMIds);
//    }
//    
//    // 상세기준삭제
//    @Transactional
//    public void deleteInspectionItems(List<Long> itemIds) {
//        qualityMapper.deleteItems(itemIds); // QualityMapper의 deleteItems 메서드 호출
//    }
//    
//    public List<InspectionResultDTO> getInspectionResultList() {
//        return qualityMapper.getInspectionResultList();
//    }
//    
//    // 1. 검사 대기 목록 조회
//    @Transactional(readOnly = true)
//    public List<WorkOrderDTO> getInspectionTargets() {
//        return qualityMapper.getInspectionTargets();
//    }
//
//    // 2. 특정 제품의 검사 항목 및 허용 공차 조회
//    @Transactional(readOnly = true)
//    public List<InspectionItemDTO> getInspectionItemByProductId(String productId) {
//        return qualityMapper.findInspectionItemsByProductId(productId);
//    }
//    
//    // 3. 검사 결과 등록
//    @Transactional
//    public void registerInspectionResult(InspectionResultDTO resultDTO, Long workOrderId) {
//        // 1. Inspection 테이블에 데이터 삽입
//        InspectionDTO inspectionDTO = new InspectionDTO();
//        inspectionDTO.setInspectionType("QC001");
//        inspectionDTO.setProductId(resultDTO.getProductId());
//        inspectionDTO.setProcessId(resultDTO.getProcessId());
//        inspectionDTO.setEmpId(resultDTO.getEmpId());
//        inspectionDTO.setLotId(resultDTO.getLotId());
//        qualityMapper.insertInspection(inspectionDTO);
//
//        // 2. Inspection_Result 테이블에 데이터 삽입
//        resultDTO.setInspectionId(inspectionDTO.getInspectionId());
//        qualityMapper.insertInspectionResult(resultDTO);
//
//        // 3. work_order 테이블 상태 업데이트
//        workOrderMapper.updateWorkOrderStatus(workOrderId);
//    }
//}