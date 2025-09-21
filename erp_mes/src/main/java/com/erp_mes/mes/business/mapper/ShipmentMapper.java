package com.erp_mes.mes.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;

@Mapper
public interface ShipmentMapper {
	
	// 등록, 생산중인 수주 목록 조회
	List<OrderDTO> getStatusOrder();

	// 출하 등록 모달창 -> 수주 목록에서 선택 -> 선택한 수주 id를 orders_detail 테이블에서 참조 -> product 테이블과 조인해서 재고량 가져옴
    List<OrderDetailDTO> getOrderDetailWithStock(@Param("orderId") String orderId);

}
