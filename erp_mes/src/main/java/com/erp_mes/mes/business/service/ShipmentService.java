package com.erp_mes.mes.business.service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.business.dto.ShipmentDTO;
import com.erp_mes.mes.business.dto.ShipmentDetailDTO;
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
	
	@Transactional
	public String createShipment(ShipmentDTO shipmentDTO) {
		// 1) 출하번호 생성 (SHI-yyyyMMdd-XXXX)
        int count = shipmentMapper.countShipment();
        int next = count + 1;
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seqPart = String.format("%04d", next);
        String shipmentId = "SHI-" + datePart + "-" + seqPart;
        shipmentDTO.setShipmentId(shipmentId);

        // 2) orderId를 사용하여 clientId와 deliveryDate를 DB에서 조회
        String orderId = shipmentDTO.getOrderId();
        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("수주번호가 누락되었습니다.");
        }
        
        OrderDTO orderInfo = shipmentMapper.getOrderInfoByOrderId(orderId);
        if (orderInfo == null) {
            throw new IllegalArgumentException("존재하지 않는 수주번호입니다: " + orderId);
        }
        
        shipmentDTO.setClientId(orderInfo.getClientId());
        shipmentDTO.setDeliveryDate(orderInfo.getDeliveryDate());
        // 초기 상태를 '부분출하'로 설정 모든 품목이 출하 완료일 경우 '출하완료'로 변경
        shipmentDTO.setShipmentStatus("PARTIAL"); 

        // 3) shipment_detail insert 및 상태 결정
        List<ShipmentDetailDTO> details = shipmentDTO.getItems();
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("출하할 품목이 없습니다.");
        }

        // 모든 품목이 출하 완료 상태인지 확인하는 변수
        boolean allItemsShipped = true;

        int seq = 1;
        for (ShipmentDetailDTO detail : details) {
        	// 출하 수량이 수주 수량을 초과했는지 유효성 검사
            if (detail.getShipmentQty() > detail.getOrderQty()) {
                throw new IllegalArgumentException("출하수량이 수주수량을 초과했습니다: 품목 " + detail.getProductName());
            }

            detail.setShipmentId(shipmentDTO.getShipmentId());
            detail.setOrderId(shipmentDTO.getOrderId());
            detail.setClientId(shipmentDTO.getClientId());
            detail.setId(seq);

            // 출하수량에 따른 상태 결정
            if (detail.getShipmentQty() == 0) {
                detail.setShipmentDetailStatus("NOTSHIPPED");
                allItemsShipped = false;
            } else if (detail.getShipmentQty() < detail.getOrderQty()) { 
                detail.setShipmentDetailStatus("PARTIAL");
                allItemsShipped = false;
            } else {
                detail.setShipmentDetailStatus("COMPLETION");
            }

            shipmentMapper.insertShipmentDetail(detail);
            seq++;
        }
        
        // 모든 품목이 '출하완료' 상태이면, 전체 출하 상태도 '출하완료'로 변경
        if (allItemsShipped) {
            shipmentDTO.setShipmentStatus("COMPLETION");
        }

        // 4) shipment insert
        shipmentMapper.insertShipment(shipmentDTO);

        return shipmentDTO.getShipmentId();
	}

	public List<ShipmentDTO> getAllShipment() {
		
		return shipmentMapper.getAllShipment();
	}

	public List<ShipmentDetailDTO> getShipmentDetailsByShipmentId(String shipmentId) {
		
		return shipmentMapper.getShipmentDetailsByShipmentId(shipmentId);
	}
	
	
	
}
