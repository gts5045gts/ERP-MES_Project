package com.erp_mes.mes.quality.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.quality.dto.InspectionDTO;
import com.erp_mes.mes.quality.dto.InspectionFMDTO;
import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import com.erp_mes.mes.quality.entity.InspectionFM;
import com.erp_mes.mes.quality.entity.InspectionItem;
import com.erp_mes.mes.quality.mapper.QualityMapper;
import com.erp_mes.mes.quality.repository.InspectionFMRepository;
import com.erp_mes.mes.quality.repository.InspectionItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class InspectionService {

	private final QualityMapper qualityMapper;
	private final InspectionFMRepository inspectionFMRepository;
	private final InspectionItemRepository inspectionItemRepository;
	
	public InspectionService(QualityMapper qualityMapper, InspectionFMRepository inspectionFMRepository,InspectionItemRepository inspectionItemRepository) {
		super();
		this.qualityMapper = qualityMapper;
		this.inspectionFMRepository = inspectionFMRepository;
		this.inspectionItemRepository = inspectionItemRepository;
	}

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
        return inspectionItemRepository.findAll().stream()
                .map(entity -> {
                    InspectionItemDTO dto = new InspectionItemDTO();
                    dto.setItemId(entity.getItemId());
                    dto.setProductId(entity.getProductId());
                    dto.setInspectionFMId(entity.getInspectionFMId());
                    dto.setToleranceValue(entity.getToleranceValue());
                    dto.setUnit(entity.getUnit());
                    return dto;
                })
                .collect(Collectors.toList());
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
        // DTO를 JPA 엔티티로 변환
        InspectionItem inspectionItem = InspectionItem.builder()
                .productId(inspectionItemDTO.getProductId())
                .inspectionFMId(inspectionItemDTO.getInspectionFMId())
                .toleranceValue(inspectionItemDTO.getToleranceValue())
                .unit(inspectionItemDTO.getUnit())
                .build();
        
        // JPA Repository를 사용하여 데이터베이스에 저장
        inspectionItemRepository.save(inspectionItem);
    }
    
    @Transactional
    public void deleteInspectionRecords(List<Long> inspectionFMIds) {
        inspectionFMRepository.deleteAllByIdInBatch(inspectionFMIds);
    }
}