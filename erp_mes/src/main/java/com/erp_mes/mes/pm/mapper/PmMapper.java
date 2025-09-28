package com.erp_mes.mes.pm.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erp_mes.erp.personnel.dto.PersonnelDTO;
import com.erp_mes.mes.pm.dto.BomDTO;
import com.erp_mes.mes.pm.dto.OrdersDTO;
import com.erp_mes.mes.pm.dto.OrdersDetailDTO;
import com.erp_mes.mes.pm.dto.ProductDTO;
import com.erp_mes.mes.pm.dto.ProductPlanDTO;
import com.erp_mes.mes.pm.dto.WorkOrderDTO;
import com.erp_mes.mes.pm.dto.WorkOrderShortageDTO;

@Mapper
public interface PmMapper {

	// 생산계획
	List<ProductPlanDTO> getProductPlanList();

	// 제품명 셀렉박스
	List<ProductDTO> getProductName();

	// 담당자 셀렉트박스
	List<PersonnelDTO> getEmpInfo();

	// 생산계획 등록
	int insertProductPlan(ProductPlanDTO productPlanDTO);

	// 작업지시서 리스트
	List<WorkOrderDTO> getWorkOrderList();

	// 등록된 수주
	List<OrdersDTO> getOrderId();

	// 수주번호에 따른 제품
	List<OrdersDetailDTO> getOrdersProduct(Map<String, Object> map);

	// possible 상태인 생산계획만 가져오기
	List<ProductPlanDTO> getPlanList();

	// 생산계획 아이디로 제품명과 생산수량 라우트 그룹 라인명 들고오기
	ProductPlanDTO getWorkOderInfo(String planId);

	// bom 필요 자재수와 창고 자재 수 비교 
	List<BomDTO> getWorkOderInventory(String planId);

	// 작업지시 등록
	int insertWorkOrder(WorkOrderDTO workOrderDTO);

	// 작업지시 등록하면 생산계획 상태값 변경
	void updatePlanStatus(String planId);
	
	// bomId로 자재 찾기
	List<BomDTO> getMaterialsByBomId(String bomId);

	// 작업지시 상태
	int updateWorkOrderStatus(Long workOrderId, String string);

	// 발주자재 조회
	List<BomDTO> getPurchaseInfo(String workOrderId);

	// 발주 등록
	int insertPusrchase(WorkOrderShortageDTO workOrderShortageDTO);

	

	

}
