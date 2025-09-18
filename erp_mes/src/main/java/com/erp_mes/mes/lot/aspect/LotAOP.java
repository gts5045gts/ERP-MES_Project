package com.erp_mes.mes.lot.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.erp_mes.erp.config.util.SessionUtil;
import com.erp_mes.erp.config.util.TableMetadataManager;
import com.erp_mes.erp.config.util.TableMetadataManager.TableInfo;
import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.service.LotService;
import com.erp_mes.mes.lot.trace.TrackLot;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Aspect
@Component
@Log4j2
@RequiredArgsConstructor
public class LotAOP {

	private final LotService lotService;

//	@AfterReturning(pointcut = "execution(* com.erp_mes.mes..service.*Service.*(com.erp_mes.mes.lot.dto.LotDTO))", returning = "targetId")
	public void LotTraceAspect(JoinPoint joinPoint, Object targetId) throws Throwable {
//		log.info("★★★★★★★★★★★★★★★ 메서드 정보 : " + joinPoint.getSignature().toShortString());
//		log.info("★★★★★★★★★★★★★★★ 파라미터 정보 : " + Arrays.toString(joinPoint.getArgs()));

		Object arg = joinPoint.getArgs()[0]; // 첫번째 인자가 LotDTO 고정일 때

		if (arg instanceof LotDTO lotDTO) {

			if (targetId instanceof String) {
				lotDTO.setTargetId((String) targetId); // targetId 세팅
			}
			// 비즈니스 처리(LOT 생성·연관 저장)는 서비스 메서드가 담당
//			String lotId = lotService.createLotWithRelations(lotDTO);
//			lotDTO.setLotId(lotId);// LOT ID 생성 및 저장
		}
	}

	@Around("@annotation(trackLot)")
	public Object traceLot(ProceedingJoinPoint pjp, TrackLot trackLot) throws Throwable {
		log.info("AOP 진입, TrackLot: " + trackLot);

		Object result = null;
		LotDTO lotDTO = null;

		try {
			// 핵심 메서드 실행
			result = pjp.proceed();
			HttpSession session = SessionUtil.getSession();
			Object obj = session.getAttribute("dto");
			TableInfo tableInfo = TableMetadataManager.getTableInfo(obj);
//			tableInfo.getTableName
//			tableInfo.pkColumnName
			log.info("DTO 객체 정보 : " + obj);
			log.info("테이블 정보 : " + tableInfo);
			log.info("result 정보 : " + result);
			
			// 반환값이 LotDTO인지 판단
			if (result instanceof LotDTO dto) {
				lotDTO = dto;

//				lotService.createLotWithRelations(lotDTO, trackLot.domain(), trackLot.createLot(), trackLot.linkParent());
			}

			return result; // 반드시 반환

		} catch (Exception e) {
			log.error("traceLot 처리 중 예외 발생", e);
			// 필요시 예외 재던지기 가능
			throw e;

		} finally {

		}
	}

}
