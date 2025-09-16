package com.erp_mes.mes.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.pm.dto.ProductDTO;

@Mapper
public interface BusinessMapper {
	List<OrderDTO> getAllOrder();
	
	// 오늘 날짜의 가장 최근 수주 번호 조회
    String selectMaxOrderIdForToday(@Param("datePrefix") String datePrefix);
	
	void insertOrder(OrderDTO orderDto);
	
	// 품목 리스트
	List<ProductDTO> getAllProduct();
	
	// 수주 상세 목록
	List<OrderDetailDTO> getOrderDetailsByOrderId(@Param("orderId")String orderId);
}
