package com.erp_mes.mes.lot.aspect;

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
import com.erp_mes.mes.lot.service.LotService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Aspect
@Component
@Log4j2
@RequiredArgsConstructor
public class LotAOP {

private final LotService lotService;

//	@Before("execution(* com.erp_mes.mes..controller.*Controller.*(com.erp_mes.mes.lot.dto.LotDTO))")
//	@Before("execution(* *(com.erp_mes.mes.lot.dto.LotDTO))")
	@AfterReturning(pointcut = "execution(* com.erp_mes.mes.lot.service.LotService.registWareHouse(..))", returning = "targetId")
	public void LotTraceAspect (JoinPoint joinPoint, Object targetId) throws Throwable {
//		log.info("★★★★★★★★★★★★★★★ 메서드 정보 : " + joinPoint.getSignature().toShortString());
//		log.info("★★★★★★★★★★★★★★★ 파라미터 정보 : " + Arrays.toString(joinPoint.getArgs()));
		
		Object arg = joinPoint.getArgs()[0]; // 첫번째 인자가 LotDTO 고정일 때
		
        if (arg instanceof LotDTO lotDTO) {
        	
            if (targetId instanceof String) {
               lotDTO.setTargetId((String) targetId);  // targetId 세팅
            }
         // 비즈니스 처리(LOT 생성·연관 저장)는 서비스 메서드가 담당
            String lotId = lotService.createLotWithRelations(lotDTO);
            lotDTO.setLotId(lotId);// LOT ID 생성 및 저장
        }
	}

	//서비스로 옮김 추후 삭제
	/*
	 * public String generateLotId(LotDTO lotDTO) {
	 * 
	 * String datePart =
	 * LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); String
	 * domain = lotDTO.getType(); LotDomain lotDomain =
	 * LotDomain.fromDomain(domain); String prefix = lotDomain.getPrefix(); Integer
	 * qty = lotDTO.getQty(); String machineId = (lotDTO.getMachineId() != null) ?
	 * lotDTO.getMachineId() : "";
	 * 
	 * int lotQty = (qty != null) ? qty : 0; // 기본값 0
	 * 
	 * //마지막 LOT 조회 String lastLotId = lotRepository.findByLastLotId(prefix,
	 * datePart, machineId); int nextSeq = 1;
	 * 
	 * if (lastLotId != null) { String[] parts = lastLotId.split("-"); nextSeq =
	 * Integer.parseInt(parts[parts.length - 1]) + 1; }
	 * 
	 * //machineId가 있을때만 포함 // LOT ID 생성 String lotId; if (machineId != null &&
	 * !machineId.isEmpty()) { lotId = String.format("%s%s-%s-%03d", prefix,
	 * datePart, machineId, nextSeq); } else { lotId = String.format("%s%s-%03d",
	 * prefix, datePart, nextSeq); }
	 * 
	 * LotMaster lot = LotMaster.builder() .lotId(lotId)
	 * .targetId(lotDTO.getTargetId()) .tableName(lotDTO.getTableName())
	 * .type(prefix) .materialCode(lotDTO.getMaterialCode()) .qty(lotQty)
	 * .machineId(machineId) .createdAt(LocalDateTime.now()) .build();
	 * 
	 * lotRepository.save(lot);
	 * 
	 * return lotId; }
	 */
	
	
	 
}
