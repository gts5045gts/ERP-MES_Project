package com.erp_mes.mes.lot.aspect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.erp_mes.erp.config.util.SessionUtil;
import com.erp_mes.erp.config.util.TableMetadataManager;
import com.erp_mes.erp.config.util.TableMetadataManager.TableInfo;
import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.dto.MaterialUsageDTO;
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
	public void traceLot(ProceedingJoinPoint pjp, TrackLot trackLot) throws Throwable {
		
		try {
			
			log.info("AOP 진입, TrackLot: " + trackLot);

//			Object result = null;
//			LotDTO lotDTO = null;

			// 핵심 메서드 실행
//				result = pjp.proceed();
			pjp.proceed();
			
			HttpSession session = SessionUtil.getSession();
			Object obj = session.getAttribute("lotDto");
			
			
			if (obj != null) {
				TableInfo tableInfo = TableMetadataManager.getTableInfo(obj);
				String pkColumnName = tableInfo.getPkColumnName();
				String tableName = tableInfo.getTableName();
				String getterPkIdName = "get" + pkColumnName.substring(0,1).toUpperCase() + pkColumnName.substring(1);
				
				log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+obj);
				
				List<MaterialUsageDTO> usages = new ArrayList<MaterialUsageDTO>();
				Object targetIdVal = null;
				Object materialType = null;
				Object parentLotId = null;
			
				targetIdVal = obj.getClass().getMethod(getterPkIdName).invoke(obj);
				//자재테이블만 자재코드를 입력함.
				if(tableName.equals("material")){
					materialType = obj.getClass().getMethod("getMaterialType").invoke(obj);	
				} else {
					parentLotId = obj.getClass().getMethod("getLotId").invoke(obj);	
				}
				
				//자재 투입이 있는 시점에만 lot_material_usage를 사용해 부모-자식 LOT 연결을 남기면 됨
				if (parentLotId != null) {
					
					usages = new ArrayList<MaterialUsageDTO>();
					MaterialUsageDTO usage1 = MaterialUsageDTO.builder()
											.parentLotId((String) parentLotId) // 이전 기록 LOT ID
											.build();
					usages.add(usage1);	
				}
				
				
				LotDTO lotDTO = LotDTO
								.builder()
								.tableName(tableName)
								.targetId((String) pkColumnName)
								.targetIdValue((String) targetIdVal)
								.materialCode((String) materialType)
								.usages(usages)
								.build();
				
				log.info("lotDTO>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+lotDTO);

				//임의로 createLot는 true로 진행함 필요시 switch 문 추가
				lotService.createLotWithRelations(lotDTO, tableName.toUpperCase(), true, false);
				//입고/공정/검사 테이블에는 lot_master의 lot_id를 업데이트 필요
				
			}
			
			if (session != null) {
			    session.removeAttribute("lotDto");
			    log.info("lotDto 속성이 세션에서 삭제.");
			}
			
			// 반환값이 LotDTO인지 판단
//				if (result instanceof LotDTO dto) {
//					lotDTO = dto;
	//
//					lotService.createLotWithRelations(lotDTO, trackLot.domain(), trackLot.createLot(), trackLot.linkParent());
//				}
//				return result; // 반드시 반환
			
		} catch (Exception e) {
			 log.error("Error during lotDTO creation or logging", e);
		}
	}
}



