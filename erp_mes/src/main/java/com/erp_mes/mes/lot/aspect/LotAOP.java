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
import com.erp_mes.mes.lot.service.LotService;
import com.erp_mes.mes.lot.service.LotUsageService;
import com.erp_mes.mes.lot.trace.TrackLot;
import com.erp_mes.mes.pop.dto.WorkResultDTO;
import com.erp_mes.mes.pop.mapper.WorkResultMapper;

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
	public void traceLot(ProceedingJoinPoint pjp, TrackLot trackLot) throws Throwable {
		
		try {
			
			log.info("AOP 진입, TrackLot: " + trackLot);

			pjp.proceed();
			
			HttpSession session = SessionUtil.getSession();
			Object obj = session.getAttribute("targetIdValue");
			
			if (obj != null) {
				
				Boolean linkParent = false;
				Boolean createLot = true;
				
				Object materialType = null;
				Object parentLotId = null;
				
				String tableName = trackLot.tableName().trim().toUpperCase();
				String targetId = trackLot.pkColumnName().trim();
				String targetIdValue = (String) obj;
				String domain = tableName;
				
				int qtyUsed = 0;
				
				List<MaterialUsageDTO> usages = new ArrayList<MaterialUsageDTO>();
				
				List<Map<String, Object>> tableInfo = lotService.getTargetInfo(tableName, targetId, targetIdValue);
				
				for (Map<String, Object> row : tableInfo) {
				    for (Map.Entry<String, Object> entry : row.entrySet()) {
				    	
				    	if(entry.getKey().equals("MATERIAL_TYPE")){
					        materialType = entry.getValue();	
				    	}
				    	
				    	if(tableName.equals("INPUT")){
				    		Object productId = entry.getValue();
				    		if (productId != null) {
								domain = "finished";
							}
						}
//				    	기존 LOT에 공정/검사/출하 이력만 추가하려면 createLot=false
//						기존 lot가져와서 parentLotId에 넣음
				    	if(entry.getKey().equals("lot_id") && tableName.equals("OUTPUT")){
			    			
//				    		Object inId = entry.getValue();
				    		
				    		//자재 투입이 있는 시점에만 lot_material_usage를 사용해 부모-자식 LOT 연결을 남김
				    		//자재 출고 등록(공장 투입)시 work_order_id를 남기고 자재번호(in_id)를 연결
							
				    		//중복으로 들어올경우 처리?
				    		
//				    		parentLotId = lotUsageService.getInputLotId(inId);
				    		parentLotId = entry.getValue();
				    		
				    		if (parentLotId != null) {
				    		
					    		List<WorkResultDTO> workOrderList = workResultMapper.workOrderWithBom(Long.parseLong(targetIdValue));
					    		
					    		for (WorkResultDTO dto : workOrderList) {
					    			 BigDecimal qty = dto.getQuantity();
					    			 qtyUsed = qty.intValue();
					    			 log.info("processNm>>>>>>>>>>>>>>>>"+dto.getProcessNm());
					    			 log.info("equipmentNm>>>>>>>>>>>>>>>>"+dto.getEquipmentNm());
					    		}
					    		
								MaterialUsageDTO usage = MaterialUsageDTO.builder()
														.parentLotId((String) parentLotId) // 자재 lotID
														.qtyUsed(qtyUsed)
														.build();
								usages.add(usage);
								linkParent = true;
								createLot=false;
				    		}
				    	}
				    }
				}
				
				LotDTO lotDTO = LotDTO
								.builder()
								.tableName(tableName)
								.targetId((String) targetId)
								.targetIdValue((String) targetIdValue)
								.materialCode((String) materialType)
								.usages(usages)
								.build();

				//임의로 createLot는 true로 진행함 필요시 switch 문 추가
			 	String lotId = lotService.createLotWithRelations(lotDTO, domain, createLot, linkParent);
				//입고/공정/검사 테이블에는 lot_master의 lot_id를 업데이트 필요
//			 	if(!tableName.equals("MATERIAL")){
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



