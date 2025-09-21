package com.erp_mes.mes.quality.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.service.CommonCodeService;
import com.erp_mes.mes.plant.dto.ProcessDTO;
import com.erp_mes.mes.plant.service.ProcessService;
import com.erp_mes.mes.pm.dto.WorkOrderDTO;
import com.erp_mes.mes.pm.mapper.WorkOrderMapper;
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
                .collect(Collectors.toMap(ProcessDTO::getProId, ProcessDTO::getProNm));

        Map<String, String> materialMap = stockService.getMaterialList().stream()
                .collect(Collectors.toMap(MaterialDTO::getMaterialId, MaterialDTO::getMaterialName));

        items.forEach(item -> {
            if (item.getProId() != null) {
                item.setProNm(processMap.get(item.getProId()));
            }
            if (item.getMaterialId() != null) {
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
    
    @Transactional(readOnly = true)
    public List<InspectionTargetDTO> getIncomingInspectionTargets() {
        List<InspectionTargetDTO> targets = qualityMapper.getIncomingInspectionTargets();
        Map<String, String> qcTypeMap = getQcTypeMap();
        targets.forEach(target -> target.setInspectionTypeName(qcTypeMap.get(target.getInspectionType())));
        return targets;
    }

    @Transactional(readOnly = true)
    public List<InspectionTargetDTO> getProcessInspectionTargets() {
        List<InspectionTargetDTO> targets = qualityMapper.getProcessInspectionTargets();
        Map<String, String> qcTypeMap = getQcTypeMap();
        targets.forEach(target -> target.setInspectionTypeName(qcTypeMap.get(target.getInspectionType())));
        return targets;
    }

    @Transactional(readOnly = true)
    public List<InspectionTargetDTO> getPackagingInspectionTargets() {
        List<InspectionTargetDTO> targets = qualityMapper.getPackagingInspectionTargets();
        Map<String, String> qcTypeMap = getQcTypeMap();
        targets.forEach(target -> target.setInspectionTypeName(qcTypeMap.get(target.getInspectionType())));
        return targets;
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
    public List<InspectionItemDTO> getInspectionItemByProcessId(Long processId) {
        return qualityMapper.findInspectionItemsByProcessId(processId);
    }
    
    @Transactional
    public void registerInspection(InspectionRegistrationRequestDTO requestDTO) {
        // 1. INSPECTION 테이블에 데이터 삽입
        InspectionDTO inspectionDTO = new InspectionDTO();
        inspectionDTO.setInspectionType(requestDTO.getInspectionType());
        inspectionDTO.setEmpId(requestDTO.getEmpId());
        inspectionDTO.setLotId(requestDTO.getLotId());

        if ("WorkOrder".equals(requestDTO.getTargetSource())) {
            inspectionDTO.setProductId(requestDTO.getProductId());
            inspectionDTO.setProcessId(requestDTO.getProcessId());
        }
        
        qualityMapper.insertInspection(inspectionDTO);
        Long newInspectionId = inspectionDTO.getInspectionId();

        // 2. INSPECTION_RESULT 테이블에 데이터 삽입
        for (InspectionResultDataDTO resultData : requestDTO.getInspectionResults()) {
            InspectionResultDTO resultDTO = new InspectionResultDTO();
            resultDTO.setInspectionId(newInspectionId);
            resultDTO.setItemId(resultData.getItemId());
            resultDTO.setResult(resultData.getResult());
            resultDTO.setRemarks(resultData.getRemarks());
            qualityMapper.insertInspectionResult(resultDTO);
        }

        // 3. 원본 테이블 상태 업데이트
        if ("WorkOrder".equals(requestDTO.getTargetSource())) {
            qualityMapper.updateWorkOrderStatus(requestDTO.getTargetId());
        } else if ("Receiving".equals(requestDTO.getTargetSource())) {
            qualityMapper.updateInputStatus(requestDTO.getTargetId());
        }
    }
}