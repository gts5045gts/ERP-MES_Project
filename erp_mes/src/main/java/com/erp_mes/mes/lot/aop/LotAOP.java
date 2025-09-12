package com.erp_mes.mes.lot.aop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.erp_mes.mes.lot.constant.LotDomain;
import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.entity.LotMaster;
import com.erp_mes.mes.lot.repository.LotRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Aspect
@Component
@Log4j2
@RequiredArgsConstructor
@Transactional
public class LotAOP {

private final LotRepository lotRepository;

//	@Before("execution(* com.erp_mes.mes..controller.*Controller.*(com.erp_mes.mes.lot.dto.LotDTO))")
//	@Before("execution(* *(com.erp_mes.mes.lot.dto.LotDTO))")
	@AfterReturning(pointcut = "execution(* com.erp_mes.mes.lot.service.LotService.registWareHouse(..))", returning = "targetId")
	public void aopTest(JoinPoint joinPoint, Object targetId) {
		log.info("★★★★★★★★★★★★★★★ 메서드 정보 : " + joinPoint.getSignature().toShortString());
		log.info("★★★★★★★★★★★★★★★ 파라미터 정보 : " + Arrays.toString(joinPoint.getArgs()));
		
		for (Object arg : joinPoint.getArgs()) {
	        if (arg instanceof LotDTO lotDTO) {
	            if (targetId instanceof String) {
	                lotDTO.setTargetId((String) targetId);  // targetId 세팅
	                generateLotId(lotDTO);       // LOT ID 생성 및 저장
	            }
	        }
	    }
	}

	
	public void generateLotId(LotDTO lotDTO) {
	  
		String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String domain = lotDTO.getType();
		LotDomain lotDomain = LotDomain.fromDomain(domain);
		String prefix = lotDomain.getPrefix();
		Integer qty = lotDTO.getQty();
		String machineId = (lotDTO.getMachineId() != null) ? lotDTO.getMachineId() : "";

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
       lot.setTargetId(lotDTO.getTargetId());
       lot.setTableName(lotDTO.getTableName());
       lot.setLotId(lotId);
       lot.setType(prefix);
       lot.setMaterialCode(lotDTO.getMaterialCode());
       lot.setQty(lotQty);
       lot.setMachineId(machineId);
       lot.setCreatedAt(LocalDateTime.now());
       
       lotRepository.save(lot);
	}
	 
}
