package com.erp_mes.mes.lot.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.query.NativeQuery;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Service;

import com.erp_mes.mes.lot.constant.LotDomain;
import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.dto.LotDetailDTO;
import com.erp_mes.mes.lot.dto.MaterialUsageDTO;
import com.erp_mes.mes.lot.entity.LotMaster;
import com.erp_mes.mes.lot.entity.LotMaterialUsage;
import com.erp_mes.mes.lot.mapper.LotMapper;
import com.erp_mes.mes.lot.repository.LotMaterialUsageRepository;
import com.erp_mes.mes.lot.repository.LotProcessHistoryRepository;
import com.erp_mes.mes.lot.repository.LotRepository;
import com.erp_mes.mes.plant.dto.ProcessDTO;
import com.erp_mes.mes.pop.dto.WorkResultDTO;
import com.erp_mes.mes.stock.mapper.WareMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class LotService {

	private final LotRepository lotRepository;
	private final LotMaterialUsageRepository usageRepository;
	private final LotMapper lotMapper;
	private final WareMapper wareMapper;
	
	@PersistenceContext
    private EntityManager entityManager;

	public String createLotWithRelations(LotDTO lotDTO, String domain, boolean createLot, boolean linkParent) {
	    String lotId = null;
	    LotMaster lot = null;

	    try {
	    	// 1. LOT 생성 및 lot_master 저장
		    if (createLot) {
		        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		        LotDomain lotDomain = LotDomain.fromDomain(domain.toLowerCase().trim());
		        String prefix = lotDomain.getPrefix();
//		        Integer qty = lotDTO.getQty();
		        String machineId = (lotDTO.getMachineId() != null) ? lotDTO.getMachineId() : "";
//		        int lotQty = (qty != null) ? qty : 0;

		        String lastLotId = lotRepository.findByLastLotId(prefix, datePart, machineId);
		        lotId = generateLotId(prefix, datePart, machineId, lastLotId);

		        lot = LotMaster.builder()
		            .lotId(lotId)
		            .targetId(lotDTO.getTargetId())
		            .targetIdValue(lotDTO.getTargetIdValue())
		            .tableName(lotDTO.getTableName())
		            .type(prefix)
		            .materialCode(lotDTO.getMaterialCode())
		            .machineId(machineId)
		            .workOrderId(lotDTO.getWorkOrderId())
		            .createdAt(LocalDateTime.now())
		            .build();

		        lotRepository.save(lot);
		        lotDTO.setLotId(lotId); // 생성된 LOT ID 설정
		    } else {
				// createLot==false : 기존 LOT 정보를 사용
		        lotId = lotDTO.getLotId();
		        if (lotId != null) {
		            lot = lotRepository.getReferenceById(lotId);
		        }
		    }

		    // 2. 자재 사용 기록 (부모-자식 LOT 연결)
		    if (linkParent && lotDTO.getUsages() != null && !lotDTO.getUsages().isEmpty()) {
		        for (MaterialUsageDTO usageDTO : lotDTO.getUsages()) {
		            LotMaster parentLot = lotRepository.getReferenceById(usageDTO.getParentLotId());
		            String childLotId = Optional.ofNullable(usageDTO.getChildLotId()).orElse(lotId);
		            LotMaster childLot = lotRepository.getReferenceById(childLotId);

		            LotMaterialUsage usage = LotMaterialUsage.builder()
		                .parentLot(parentLot)
		                .childLot(childLot)
		                .qtyUsed(usageDTO.getQtyUsed())
		                .createdAt(LocalDateTime.now())
		                .build();
		            usageRepository.save(usage);
		        }
		    }

		    //작업지시 테이블 참조로 변경
			/*
			 * // 3. 공정 이력 기록 (processes는 리스트 유무로 분기) if (lotDTO.getProcesses() != null &&
			 * !lotDTO.getProcesses().isEmpty() && lot != null) { for (ProcessHistoryDTO
			 * processDTO : lotDTO.getProcesses()) { LotProcessHistory history =
			 * LotProcessHistory.builder() .lot(lot)
			 * .processCode(processDTO.getProcessCode())
			 * .machineId(processDTO.getMachineId()) .operator(processDTO.getOperator())
			 * .processStart(processDTO.getProcessStart())
			 * .processEnd(processDTO.getProcessEnd()) .inputQty(processDTO.getInputQty())
			 * .resultQty(processDTO.getResultQty()) .scrapQty(processDTO.getScrapQty())
			 * .createdAt(LocalDateTime.now()) .build(); historyRepository.save(history); }
			 * }
			 */

		    
			
		} catch (Exception e) {
			log.error("lot생성 처리 중 예외 발생", e);
			throw e;
		}
	    
	    return lotId;
	    
	}
	
	public String generateLotId(String prefix, String datePart, String machineId, String lastLotId) {
	    int nextSeq = 1;
	    if (lastLotId != null) {
	        String[] parts = lastLotId.split("-");
	        try {
	            nextSeq = Integer.parseInt(parts[parts.length - 1]) + 1;
	        } catch (NumberFormatException e) {
	            nextSeq = 1; // 안전하게 기본값 1 할당
	        }
	    }
	    if (machineId != null && !machineId.isEmpty()) {
	        return String.format("%s%s-%s-%03d", prefix, datePart, machineId, nextSeq);
	    } else {
	        return String.format("%s%s-%03d", prefix, datePart, nextSeq);
	    }
	}

	//targetTable 조회
	public List<Map<String, Object>> getTargetInfo(String tableName, String targetId, Object targetIdValue) {
		
        String sql = "SELECT * FROM " + tableName + " WHERE " + targetId + " = :targetIdValue";

        NativeQuery<?> nativeQuery = entityManager.createNativeQuery(sql).unwrap(NativeQuery.class);
        
        if (targetIdValue instanceof String) {
			targetIdValue = (String) targetIdValue;
		} else if (targetIdValue instanceof Integer) {
			targetIdValue = (Integer) targetIdValue;
		} else if (targetIdValue instanceof Long) {
			targetIdValue = (Long) targetIdValue;
		}

        nativeQuery.setParameter("targetIdValue", targetIdValue);

        // Hibernate 6부터는 아래와 같이 Transformer 대신 TupleTransformer 사용 권장
        nativeQuery.setTupleTransformer((tuple, aliases) -> {
            Map<String, Object> result = new java.util.HashMap<>();
            for (int i = 0; i < aliases.length; i++) {
                result.put(aliases[i], tuple[i]);
            }
            return result;
        });

        return (List<Map<String, Object>>) nativeQuery.getResultList();
	}

	public void updateLotId(String tableName, String targetId, Object targetIdValue, String LotId) {
		
		if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
		    throw new IllegalArgumentException("Invalid table name format");
		}

		if (!targetId.matches("^[a-zA-Z0-9_]+$")) {
		    throw new IllegalArgumentException("Invalid column name format");
		}
		
		if (targetIdValue instanceof String) {
			targetIdValue = (String) targetIdValue;
		} else if (targetIdValue instanceof Integer) {
			targetIdValue = (Integer) targetIdValue;
		} else if (targetIdValue instanceof Long) {
			targetIdValue = (Long) targetIdValue;
		}
		
		String sql = "UPDATE "+ tableName +" SET LOT_ID = :lot_id WHERE "+ targetId +" = :targetIdValue";
		NativeQuery<?> nativeQuery = entityManager.createNativeQuery(sql).unwrap(NativeQuery.class);
        nativeQuery.setParameter("targetIdValue", targetIdValue);
        nativeQuery.setParameter("lot_id", LotId);
        nativeQuery.executeUpdate();
	}

	public List<LotDTO> getLotTrackingList(int page, int size) {
		
		int offset = page * size;

        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("size", size);
		
		return lotMapper.lotListWithPaged(params);
	}

	public Long getPopLotId(String popLotId) {
		return lotRepository.findPopByworkOrderId(popLotId);
	}

	public List<LotMaster> getOutPutLotIdAll(Object workOrderId) {
		workOrderId = Long.parseLong(String.valueOf(workOrderId));
		return lotRepository.findByWorkOrderId((Long) workOrderId);
	}

	public int getOutPutQty(String targetIdValue) {
		Map<String, Object> output = wareMapper.selectOutputById(targetIdValue);
		
		if(output == null) {
            throw new RuntimeException("출고 정보를 찾을 수 없습니다.");
        }
		
		Integer outCount = ((Number) output.get("OUT_COUNT")).intValue();
		
		return outCount;
	}

	public List<Map<String, Object>> findByProcess(String productId) {
		List<ProcessDTO> children = lotMapper.findByProcess(productId);
		
		List<Map<String, Object>> process = children.stream()
			.map(dto -> {
					Map<String, Object> map = new HashMap<>();
					map.put("proNm", dto.getProNm());
					map.put("typeNm",dto.getTypeNm());
					return map;
				})
		    .collect(Collectors.toList());
		
		return process;
	}

	public List<WorkResultDTO> findDetail(Long workOrderId) {

        Map<String, Object> params = new HashMap<>();

        return lotMapper.findDetail(workOrderId);
		
	}

	public List<LotDetailDTO> findByMaterial(String workOrderId) {
		 
		return lotRepository.findByMaterialInfo(workOrderId);
	}

	public List<LotDetailDTO> findByEquipment(String productId) {
		return lotRepository.findByEquipmenInfo(productId);
	}
}