package com.erp_mes.mes.lot.aspect;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.erp_mes.erp.config.util.SessionUtil;
import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.dto.MaterialUsageDTO;
import com.erp_mes.mes.lot.entity.LotMaster;
import com.erp_mes.mes.lot.entity.LotMaterialUsage;
import com.erp_mes.mes.lot.service.LotService;
import com.erp_mes.mes.lot.service.LotUsageService;
import com.erp_mes.mes.lot.trace.TrackLot;
import com.erp_mes.mes.pm.dto.WorkOrderDTO;
import com.erp_mes.mes.pop.dto.WorkResultDTO;
import com.erp_mes.mes.pop.mapper.WorkResultMapper;
import com.erp_mes.mes.pop.repository.WorkResultRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Aspect
@Component
@Log4j2
@RequiredArgsConstructor
public class LotAOP {

	private final LotService lotService;
	private final LotUsageService lotUsageService;
	private final WorkResultMapper workResultMapper;
	
//	프로세스별 예외사항 때문에 db조회 방식으로 변경함
	@Around("@annotation(trackLot)")
	public Object traceLot(ProceedingJoinPoint pjp, TrackLot trackLot) throws Throwable {
		
		Object result = null;
		
		try {
			
			result = pjp.proceed();
			
			HttpSession session = SessionUtil.getSession();
			Object targetIdValue = session.getAttribute("targetIdValue");
			
			if (targetIdValue != null) {
				
				Boolean linkParent = false;
				Boolean createLot = true;
				
				Object materialType = null;
				Object parentLotId = null;
				Object workOrderId = null;
				
				String tableName = trackLot.tableName().trim().toUpperCase();
				String targetId = trackLot.pkColumnName().trim();
				String domain = tableName;
				String targetVal = null;
				
				Long popWorkOrderID = null;
				
				int qtyUsed = 0;
				
				List<MaterialUsageDTO> usages = new ArrayList<MaterialUsageDTO>();
				
				List<Map<String, Object>> tableInfo = lotService.getTargetInfo(tableName, targetId, targetIdValue);
				
				if (tableInfo == null) {
					return null;
				}
				
				for (Map<String, Object> row : tableInfo) {
				    for (Map.Entry<String, Object> entry : row.entrySet()) {
				    	
				    	if(entry.getKey().equals("MATERIAL_TYPE")){
					        materialType = entry.getValue();	
				    	}
				    	
				    	if(entry.getKey().equals("WORK_ORDER_ID")){
				    		workOrderId = entry.getValue();
				    	}
				    	
				    	if(entry.getKey().equals("LOT_ID") && tableName.equals("INSPECTION") ){
				    		
				    		Object popLotId = entry.getValue();
				    		popWorkOrderID = lotService.getPopLotId((String) popLotId);
						}
				    	
				    	if(tableName.equals("OUTPUT")){
				    		popWorkOrderID = Long.parseLong(String.valueOf(workOrderId));
						}
				    }
				}
				
				if(tableName.equals("WORK_RESULT")){
	    			
		    		//자재 투입이 있는 시점에만 lot_material_usage를 사용해 부모-자식 LOT 연결을 남김
		    		//자재 출고 등록(공장 투입)시 work_order_id를 남기고 자재번호(in_id)를 연결
		    		
		    		if (workOrderId != null) {
		    			
		    			List<LotMaster> lotMasters = lotService.getOutPutLotIdAll(workOrderId);
		    			
		    			for (LotMaster lot : lotMasters){
		    				
		    				parentLotId = lot.getLotId();
		    				
		    				qtyUsed = lotService.getOutPutQty(lot.getTargetIdValue());
		    				
							MaterialUsageDTO usage = MaterialUsageDTO.builder()
													.parentLotId((String) parentLotId) // 자재 lotID
													.qtyUsed(qtyUsed)
													.build();
							usages.add(usage);
							linkParent = true;
		    			}
			   
//						createLot=false;
		    		}
		    	}
				
				if (targetIdValue instanceof String) {
				    targetVal = (String) targetIdValue;
				} else if (targetIdValue instanceof Integer) {
				    targetVal = Integer.toString((Integer) targetIdValue);
				} else if (targetIdValue instanceof Long) {
				    targetVal = Long.toString((Long) targetIdValue);
				} else {
				    // 그 외 타입은 toString()을 호출하거나 null 처리
				    targetVal = (targetIdValue != null) ? targetIdValue.toString() : null;
				}
				
				LotDTO lotDTO = LotDTO
								.builder()
								.tableName(tableName)
								.targetId((String) targetId)
								.targetIdValue(targetVal)
								.materialCode((String) materialType)
								.usages(usages)
								.workOrderId(popWorkOrderID)
								.build();

				//임의로 createLot는 true로 진행함 필요시 switch 문 추가
			 	String lotId = lotService.createLotWithRelations(lotDTO, domain, createLot, linkParent);
				//입고/공정/검사 테이블에는 lot_master의 lot_id를 업데이트 필요
		 		if(createLot){
			        lotService.updateLotId(tableName, targetId, targetIdValue, lotId);
		    	}
			}
			
			if (session != null) {
			    session.removeAttribute("targetIdValue");
			}
			
			
		} catch (Exception e) {
			 log.error("Error during lotDTO creation or logging", e);
		}
		
		return result;
	}

	/*
	 * @Around("@annotation(trackLot)") public void traceLot(ProceedingJoinPoint
	 * pjp, TrackLot trackLot) throws Throwable {
	 * 
	 * try {
	 * 
	 * log.info("AOP 진입, TrackLot: " + trackLot);
	 * 
	 * // Object result = null; // LotDTO lotDTO = null;
	 * 
	 * // 핵심 메서드 실행 // result = pjp.proceed(); pjp.proceed();
	 * 
	 * HttpSession session = SessionUtil.getSession(); Object obj =
	 * session.getAttribute("lotDto");
	 * 
	 * 
	 * if (obj != null) { TableInfo tableInfo =
	 * TableMetadataManager.getTableInfo(obj); String pkColumnName =
	 * tableInfo.getPkColumnName(); String tableName = tableInfo.getTableName();
	 * String getterPkIdName = "get" + pkColumnName.substring(0,1).toUpperCase() +
	 * pkColumnName.substring(1);
	 * 
	 * log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+obj);
	 * 
	 * List<MaterialUsageDTO> usages = new ArrayList<MaterialUsageDTO>(); Object
	 * targetIdVal = null; Object materialType = null; Object parentLotId = null;
	 * 
	 * targetIdVal = obj.getClass().getMethod(getterPkIdName).invoke(obj); //자재테이블만
	 * 자재코드를 입력함. if(tableName.equals("material")){ materialType =
	 * obj.getClass().getMethod("getMaterialType").invoke(obj); } else { parentLotId
	 * = obj.getClass().getMethod("getLotId").invoke(obj); }
	 * 
	 * //자재 투입이 있는 시점에만 lot_material_usage를 사용해 부모-자식 LOT 연결을 남기면 됨 if (parentLotId
	 * != null) {
	 * 
	 * usages = new ArrayList<MaterialUsageDTO>(); MaterialUsageDTO usage1 =
	 * MaterialUsageDTO.builder() .parentLotId((String) parentLotId) // 이전 기록 LOT ID
	 * .build(); usages.add(usage1); }
	 * 
	 * LotDTO lotDTO = LotDTO .builder() .tableName(tableName) .targetId((String)
	 * pkColumnName) .targetIdValue((String) targetIdVal) .materialCode((String)
	 * materialType) .usages(usages) .build();
	 * 
	 * log.info("lotDTO>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+lotDTO);
	 * 
	 * //임의로 createLot는 true로 진행함 필요시 switch 문 추가
	 * lotService.createLotWithRelations(lotDTO, tableName.toUpperCase(), true,
	 * false); //입고/공정/검사 테이블에는 lot_master의 lot_id를 업데이트 필요
	 * 
	 * }
	 * 
	 * if (session != null) { session.removeAttribute("lotDto");
	 * log.info("lotDto 속성이 세션에서 삭제."); }
	 * 
	 * 
	 * } catch (Exception e) { log.error("Error during lotDTO creation or logging",
	 * e); } }
	 */
}



