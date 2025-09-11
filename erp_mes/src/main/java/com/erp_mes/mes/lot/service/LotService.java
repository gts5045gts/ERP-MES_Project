package com.erp_mes.mes.lot.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.erp_mes.mes.lot.constant.LotDomain;
import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.entity.LotMaster;
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

	public String generateLotId(String domain, Integer qty, String machineId) {
		String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		
		 LotDomain lotDomain = LotDomain.fromDomain(domain);
		 String prefix = lotDomain.getPrefix();
		 
		 int lotQty = (qty != null) ? qty : 0;  // 기본값 0
		
		//마지막 LOT 조회
		String lastLotId = lotRepository.findByLastLotId(prefix, datePart, machineId);
		int nextSeq = 1;
		
		if (lastLotId != null) {
			String[] parts = lastLotId.split("-");
			nextSeq = Integer.parseInt(parts[parts.length - 1]) + 1;
		}
		
		//machineId가 있을때만 포함
		 // LOT ID 생성
        String lotId;
        if (machineId != null && !machineId.isEmpty()) {
            lotId = String.format("%s%s-%s-%03d", prefix, datePart, machineId, nextSeq);
        } else {
            lotId = String.format("%s%s-%03d", prefix, datePart, nextSeq);
        }
        
        LotMaster lot = new LotMaster();
        lot.setLotId(lotId);
        lot.setType(prefix);
        lot.setMaterialCode(domain);
        lot.setQty(lotQty);
        lot.setMachineId(machineId);
        lot.setCreatedAt(LocalDateTime.now());
        
        lotRepository.save(lot);
        
        return lotId;

	}

	public void registLot(LotDTO lotDTO) {
		
	}

}
