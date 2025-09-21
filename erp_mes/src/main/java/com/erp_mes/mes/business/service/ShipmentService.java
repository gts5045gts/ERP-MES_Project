package com.erp_mes.mes.business.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.business.mapper.ShipmentMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ShipmentService {
	private final ShipmentMapper shipmentMapper;
	
	public ShipmentService(ShipmentMapper shipmentMapper) {
		this.shipmentMapper = shipmentMapper;
	}
	
	// 등록, 생산중인 수주 목록
	public List<OrderDTO> getStatusOrder() {
		return shipmentMapper.getStatusOrder();
	}
	
	public List<OrderDetailDTO> getOrderDetail(String orderId) {
        // product_id로 product 테이블의 quantity값 가져오는 것도 포함
        return shipmentMapper.getOrderDetailWithStock(orderId);
    }
}
