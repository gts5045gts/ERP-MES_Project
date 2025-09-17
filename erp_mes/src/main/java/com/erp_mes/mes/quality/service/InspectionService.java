package com.erp_mes.mes.quality.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.pm.dto.ProductDTO;
import com.erp_mes.mes.pm.service.ProductBomService;
import com.erp_mes.mes.quality.dto.InspectionFMDTO;
import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import com.erp_mes.mes.quality.entity.InspectionFM;
import com.erp_mes.mes.quality.mapper.QualityMapper;
import com.erp_mes.mes.quality.repository.InspectionFMRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class InspectionService {

	private final QualityMapper qualityMapper;
	private final InspectionFMRepository inspectionFMRepository;
	private final ProductBomService productBomService;
	
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

    // 오른쪽 테이블 (검사 항목별 허용 공차) 조회 로직
    @Transactional(readOnly = true)
    public List<InspectionItemDTO> getInspectionItems() {
    	// MyBatis 매퍼를 통해 조인된 InspectionItem 데이터 가져오기
        List<InspectionItemDTO> items = qualityMapper.findAllItems();

        // 제품 이름을 위한 맵 생성 (성능 최적화)
        Map<String, String> productMap = productBomService.getProductList().stream()
                .collect(Collectors.toMap(ProductDTO::getProductId, ProductDTO::getProductName));
        
        // 데이터 가공
        items.forEach(item -> {
            // productId를 이용해 productName을 DTO에 설정
            item.setProductName(productMap.get(item.getProductId()));
            // inspectionFMId를 이용해 inspectionType과 itemName을 DTO에 설정 (이미 매퍼에서 처리되었다고 가정)
        });
		return items;
    }
    
    // 왼쪽 테이블 (검사 유형별 기준) 등록 로직
    @Transactional
    public void registerInspectionRecord(InspectionFMDTO inspectionFMDTO) {
        InspectionFM inspectionFM = InspectionFM.builder()
                .inspectionType(inspectionFMDTO.getInspectionType())
                .itemName(inspectionFMDTO.getItemName())
                .methodName(inspectionFMDTO.getMethodName())
                .build();
        inspectionFMRepository.save(inspectionFM);
    }
    
    // 오른쪽 테이블 (검사 항목별 허용 공차) 등록 로직
    @Transactional
    public void registerInspectionItem(InspectionItemDTO inspectionItemDTO) {
        // QualityMapper의 insertItem() 메서드를 호출하여 데이터를 삽입
        qualityMapper.insertItem(inspectionItemDTO);
    }
    
    // 검사기준삭제
    @Transactional
    public void deleteInspectionRecords(List<Long> inspectionFMIds) {
        inspectionFMRepository.deleteAllByIdInBatch(inspectionFMIds);
    }
    
    // 상세기준삭제
    @Transactional
    public void deleteInspectionItems(List<Long> itemIds) {
        qualityMapper.deleteItems(itemIds); // QualityMapper의 deleteItems 메서드 호출
    }
}