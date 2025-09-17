package com.erp_mes.mes.business.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.pm.dto.ProductDTO;

@Mapper
public interface BusinessMapper {
	// 수주번호 부여를 위한 orders 테이블 컬럼수 체크
	int countOrders();
	
	// 수주 등록 시 선택한 clientName값으로 client_id값 찾음
	String findClientIdByName(@Param("clientName") String clientName);

    int insertOrder(Map<String, Object> params);

    int insertOrderDetail(Map<String, Object> params);
	
    // 품목 리스트
 	List<ProductDTO> getAllProduct();
	
 	// 수주 목록
	List<OrderDTO> getAllOrder();
	
	// 수주 상세 목록
	List<OrderDetailDTO> getOrderDetailsByOrderId(@Param("orderId")String orderId);
}
