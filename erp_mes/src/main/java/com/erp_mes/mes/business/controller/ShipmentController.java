package com.erp_mes.mes.business.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.business.service.ShipmentService;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/business")
@Log4j2
public class ShipmentController {
	
	private final ShipmentService shipmentService;
	
	public ShipmentController(ShipmentService shipmentService) {
		this.shipmentService = shipmentService;
	}
	
	
	@GetMapping("/shipment")
	public String shipment() {
		
		return "business/shipment";
	}
	
	// 등록, 생산중인 수주 목록 조회
	@GetMapping("/api/shipment/orders")
	@ResponseBody
	public List<OrderDTO> getStatusOrder() {
		log.info("출하등록에서 수주 목록 조회 요청");

		return shipmentService.getStatusOrder();
	}
	
	// 선택한 수주의 상세 정보 조회
	@GetMapping("/api/shipment/ordersDetail")
	@ResponseBody
    public List<OrderDetailDTO> getOrderDetail(@RequestParam("orderId") String orderId) {
        return shipmentService.getOrderDetail(orderId);
    }
}
