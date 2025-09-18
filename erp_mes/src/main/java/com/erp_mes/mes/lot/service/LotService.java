package com.erp_mes.mes.lot.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.erp_mes.mes.lot.constant.LotDomain;
import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.dto.MaterialUsageDTO;
import com.erp_mes.mes.lot.entity.LotMaster;
import com.erp_mes.mes.lot.entity.LotMaterialUsage;
import com.erp_mes.mes.lot.repository.LotMaterialUsageRepository;
import com.erp_mes.mes.lot.repository.LotProcessHistoryRepository;
import com.erp_mes.mes.lot.repository.LotRepository;

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
	private final LotProcessHistoryRepository historyRepository;

	@Transactional
	public String createLotWithRelations(LotDTO lotDTO, String domain, boolean createLot, boolean linkParent) {
	    String lotId = null;
	    LotMaster lot = null;

	    // 1. LOT 생성 및 lot_master 저장
	    if (createLot) {
	        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	        LotDomain lotDomain = LotDomain.fromDomain(domain);
	        String prefix = lotDomain.getPrefix();
//	        Integer qty = lotDTO.getQty();
	        String machineId = (lotDTO.getMachineId() != null) ? lotDTO.getMachineId() : "";
//	        int lotQty = (qty != null) ? qty : 0;

	        String lastLotId = lotRepository.findByLastLotId(prefix, datePart, machineId);
	        lotId = generateLotId(prefix, datePart, machineId, lastLotId);

	        lot = LotMaster.builder()
	            .lotId(lotId)
	            .targetId(lotDTO.getTargetId())
	            .targetIdValue(lotDTO.getTargetIdValue())
	            .tableName(lotDTO.getTableName())
	            .type(prefix)
	            .materialCode(lotDTO.getMaterialCode())
//	            .qty(lotQty)
	            .machineId(machineId)
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

	public String registWareHouse(LotDTO lotDTO) {

//		여기에서 입고를 처리하고 WareHouse테이블에 save 해서 pk id값이 생성됨 그걸 리턴 
		return "PRD002";
	}

}
