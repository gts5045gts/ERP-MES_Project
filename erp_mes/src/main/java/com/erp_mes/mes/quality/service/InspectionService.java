package com.erp_mes.mes.quality.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.erp_mes.mes.quality.dto.InspectionDTO;
import com.erp_mes.mes.quality.dto.InspectionFMDTO;
import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import com.erp_mes.mes.quality.entity.InspectionFM;
import com.erp_mes.mes.quality.mapper.QualityMapper;
import com.erp_mes.mes.quality.repository.InspectionFMRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class InspectionService {

	private final QualityMapper qualityMapper;
	private final InspectionFMRepository inspectionFMRepository;
	
	public InspectionService(QualityMapper qualityMapper, InspectionFMRepository inspectionFMRepository) {
		super();
		this.qualityMapper = qualityMapper;
		this.inspectionFMRepository = inspectionFMRepository;
	}

	public List<InspectionFMDTO> findAllInspectionFMs() {
	    return inspectionFMRepository.findAll().stream()
	            .map(entity -> {
	                InspectionFMDTO insepctionFMDTO = new InspectionFMDTO();
	                insepctionFMDTO.setInspectionFMId(entity.getInspectionFMId());
	                insepctionFMDTO.setInspectionType(entity.getInspectionType());
	                insepctionFMDTO.setItemName(entity.getItemName());
	                insepctionFMDTO.setMethodName(entity.getMethodName());
	                return insepctionFMDTO;
	            })
	            .collect(Collectors.toList());
	}
	
    public List<InspectionItemDTO> getInspectionItems() {
        return qualityMapper.findAllItems();
    }
    
    // 왼쪽 테이블 (검사 유형별 기준) 등록
    public void registerInspectionRecord(InspectionFMDTO insepctionFMDTO) {
    	InspectionFM insepctionFM = insepctionFMDTO.toEntity();
    	
    	inspectionFMRepository.save(insepctionFM);
    }

    // 오른쪽 테이블 (검사 항목별 허용 공차) 등록
    public void registerInspectionItem(InspectionItemDTO inspectionItemDTO) {
    	qualityMapper.insertItem(inspectionItemDTO);
    }
    
    // 삭제
    public void deleteInspectionRecords(List<Long> inspectionFMIds) {
        inspectionFMRepository.deleteAllByIdInBatch(inspectionFMIds);
    }
}