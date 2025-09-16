package com.erp_mes.mes.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.mes.business.dto.OrderDTO;

@Mapper
public interface BusinessMapper {
	List<OrderDTO> getAllOrder();
	
	// 오늘 날짜의 가장 최근 수주 번호 조회
    String selectMaxOrderIdForToday(@Param("datePrefix") String datePrefix);
	
	void insertOrder(OrderDTO orderDto);
}
