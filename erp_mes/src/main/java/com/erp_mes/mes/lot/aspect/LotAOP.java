package com.erp_mes.mes.lot.aspect;

import com.erp_mes.mes.lot.entity.LotMaster;
import com.erp_mes.mes.lot.trace.TrackLot;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.service.LotService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Aspect
@Component
@Log4j2
@RequiredArgsConstructor
public class LotAOP {

	private final LotService lotService;

//	@AfterReturning(pointcut = "execution(* com.erp_mes.mes.lot.service.LotService.registWareHouse(..))", returning = "targetId")
	public void LotTraceAspect(JoinPoint joinPoint, Object targetId) throws Throwable {
//		log.info("★★★★★★★★★★★★★★★ 메서드 정보 : " + joinPoint.getSignature().toShortString());
//		log.info("★★★★★★★★★★★★★★★ 파라미터 정보 : " + Arrays.toString(joinPoint.getArgs()));

		Object arg = joinPoint.getArgs()[0]; // 첫번째 인자가 LotDTO 고정일 때

		if (arg instanceof LotDTO lotDTO) {

			if (targetId instanceof String) {
				lotDTO.setTargetId((String) targetId); // targetId 세팅
			}
			// 비즈니스 처리(LOT 생성·연관 저장)는 서비스 메서드가 담당
			String lotId = lotService.createLotWithRelations(lotDTO);
			lotDTO.setLotId(lotId);// LOT ID 생성 및 저장
		}
	}
	
	@Around("@annotation(trackLot)")
	public Object traceLot(ProceedingJoinPoint pjp, TrackLot trackLot) throws Throwable{
        log.info("AOP 진입, TrackLot: " + trackLot);

        Object result = null;
        LotDTO lotDTO = null;
        String newLot = null;

        try {
            // 핵심 메서드 실행
            result = pjp.proceed();

            // 반환값이 LotDTO인지 판단
            if (result instanceof LotDTO dto) {
                lotDTO = dto;

                // LOT 생성 필요 여부 판단
                if (trackLot.createLot()) {
                    // LOT 생성 및 관계 설정
                    newLot = lotService.createLotWithRelations(lotDTO);
                }

                // 2) 부모-자식 LOT 연결
        //            if (trackLot.linkParent() && parentLotId != null && lotId != null) {
        //                lotService.linkLots(parentLotId, newLot.getLotId(), qty);
        //            }
            }

            return result; // 반드시 반환

        }catch (Exception e) {
            log.error("traceLot 처리 중 예외 발생", e);
            // 필요시 예외 재던지기 가능
            throw e;

        } finally {
            // finally에서는 null 체크 후 안전하게 처리
            if (lotDTO != null && newLot != null) {
                // LOT ID 설정
                lotDTO.setLotId(newLot);
            }
        }
    }
	

	// 서비스로 옮김 추후 삭제
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
