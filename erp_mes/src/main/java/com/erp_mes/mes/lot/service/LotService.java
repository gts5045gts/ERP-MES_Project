package com.erp_mes.mes.lot.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.erp_mes.mes.lot.constant.LotDomain;
import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.dto.MaterialUsageDTO;
import com.erp_mes.mes.lot.dto.ProcessHistoryDTO;
import com.erp_mes.mes.lot.entitiy.LotMaterialUsage;
import com.erp_mes.mes.lot.entity.LotMaster;
import com.erp_mes.mes.lot.entity.LotProcessHistory;
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

	public String createLotWithRelations(LotDTO lotDTO) {
		String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String domain = lotDTO.getType();
		LotDomain lotDomain = LotDomain.fromDomain(domain);
		String prefix = lotDomain.getPrefix();
		Integer qty = lotDTO.getQty();
		String machineId = (lotDTO.getMachineId() != null) ? lotDTO.getMachineId() : "";

		int lotQty = (qty != null) ? qty : 0; // 기본값 0

		// 마지막 LOT 조회
		String lastLotId = lotRepository.findByLastLotId(prefix, datePart, machineId);
		int nextSeq = 1;

		if (lastLotId != null) {
			String[] parts = lastLotId.split("-");
			nextSeq = Integer.parseInt(parts[parts.length - 1]) + 1;
		}

		// machineId가 있을때만 포함
		// 1) LOT ID 생성
		String lotId;
		if (machineId != null && !machineId.isEmpty()) {
			lotId = String.format("%s%s-%s-%03d", prefix, datePart, machineId, nextSeq);
		} else {
			lotId = String.format("%s%s-%03d", prefix, datePart, nextSeq);
		}

		// 2) lot_master 저장
		LotMaster lot = LotMaster.builder().lotId(lotId).targetId(lotDTO.getTargetId()).tableName(lotDTO.getTableName())
				.type(prefix).materialCode(lotDTO.getMaterialCode()).qty(lotQty).machineId(machineId)
				.createdAt(LocalDateTime.now()).build();

		lotRepository.save(lot);

		// 3) 선택: 자재 사용 기록 저장
		if (lotDTO.getUsages() != null && !lotDTO.getUsages().isEmpty()) {
			for (MaterialUsageDTO usageDTO : lotDTO.getUsages()) {
				LotMaster parentLot = lotRepository.getReferenceById(usageDTO.getParentLotId());
				String childLotId = Optional.ofNullable(usageDTO.getChildLotId()).orElse(lotId);
				LotMaster childLot = lotRepository.getReferenceById(childLotId);

				LotMaterialUsage usage = LotMaterialUsage.builder().parentLot(parentLot).childLot(childLot)
						.qtyUsed(usageDTO.getQtyUsed()).createdAt(LocalDateTime.now()).build();
				usageRepository.save(usage);
			}
		}

		// 4) 선택: 공정 이력 기록
		if (lotDTO.getProcesses() != null && !lotDTO.getProcesses().isEmpty()) {
			for (ProcessHistoryDTO processDTO : lotDTO.getProcesses()) {
				LotProcessHistory history = LotProcessHistory.builder().lot(lot)
						.processCode(processDTO.getProcessCode()).machineId(machineId)
						.operator(processDTO.getOperator()).processStart(processDTO.getProcessStart())
						.processEnd(processDTO.getProcessEnd()).inputQty(processDTO.getInputQty())
						.resultQty(processDTO.getResultQty()).scrapQty(processDTO.getScrapQty())
						.createdAt(LocalDateTime.now()).build();
				historyRepository.save(history);
			}
		}

		return lotId;
	}

	public String registWareHouse(LotDTO lotDTO) {

//		여기에서 입고를 처리하고 WareHouse테이블에 save 해서 pk id값이 생성됨 그걸 리턴 
		return "PUR-YYYYMMDD-123";
	}

}
