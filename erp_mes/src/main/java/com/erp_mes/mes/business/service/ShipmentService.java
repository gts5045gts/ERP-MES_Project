package com.erp_mes.mes.business.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.business.dto.OutputDTO;
import com.erp_mes.mes.business.dto.ShipmentDTO;
import com.erp_mes.mes.business.dto.ShipmentDetailDTO;
import com.erp_mes.mes.business.mapper.ShipmentMapper;
import com.erp_mes.mes.stock.dto.WarehouseItemDTO;

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
	
	public List<OrderDetailDTO> getOrderDetailWithStockAndStatus(String orderId) {
        // product_id로 product 테이블의 quantity값 가져오는 것도 포함 + order_detail_status가 COMPLETION이 아닌 것들만 목록 조회
		
		// 1. 날짜 지연된 품목 목록을 먼저 조회
	    List<String> delayedProductIds = shipmentMapper.getDelayedProductIds();

	    // 2. 기존 로직으로 상세 목록을 가져옴
	    List<OrderDetailDTO> orderDetails = shipmentMapper.getOrderDetailWithStockAndStatus(orderId);

	    // 3. 각 상세 품목이 지연된 품목 목록에 포함되는지 확인하고 출하 가능 여부 설정
	    for (OrderDetailDTO detail : orderDetails) {
	        if (delayedProductIds.contains(detail.getProductId())) {
	            // 해당 품목이 지연된 품목이라면, 출하를 막기 위해 필요한 정보를 설정
	            detail.setDelayed(true);
	        }
	    }
	    return orderDetails;
		
//        return shipmentMapper.getOrderDetailWithStockAndStatus(orderId);
    }
	
	@Transactional
	public String createShipment(ShipmentDTO shipmentDTO) {
	    // 1) 수주 번호 (orderId)를 사용하여 기존 출하 번호(shipmentId)가 있는지 조회
	    String orderId = shipmentDTO.getOrderId();
	    if (orderId == null || orderId.isEmpty()) {
	        throw new IllegalArgumentException("수주번호가 누락되었습니다.");
	    }
	    
	    // 기존 출하가 있는지 확인하는 메서드 호출 (ShipmentMapper에 구현 필요)
	    String existingShipmentId = shipmentMapper.findExistingShipmentIdByOrderId(orderId);
	    
	    String shipmentId;
	    boolean isNewShipment = (existingShipmentId == null);

	    // 2) 신규 출하 또는 기존 출하 업데이트 분기 처리
	    if (isNewShipment) {
	        // 새로운 출하 번호 생성 (SHI-yyyyMMdd-XXXX)
	        int count = shipmentMapper.countShipment();
	        int next = count + 1;
	        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	        String seqPart = String.format("%04d", next);
	        shipmentId = "SHI-" + datePart + "-" + seqPart;
	        shipmentDTO.setShipmentId(shipmentId);
	        
	        // orderId로 클라이언트와 납기일 정보 조회 및 설정
	        OrderDTO orderInfo = shipmentMapper.getOrderInfoByOrderId(orderId);
	        if (orderInfo == null) {
	            throw new IllegalArgumentException("존재하지 않는 수주번호입니다: " + orderId);
	        }
	        
	        shipmentDTO.setClientId(orderInfo.getClientId());
	        shipmentDTO.setDeliveryDate(orderInfo.getDeliveryDate());
	        shipmentDTO.setShipmentStatus("PARTIAL"); // 초기 상태 설정
	        
	        // 신규 출하 레코드 INSERT
	        shipmentMapper.insertShipment(shipmentDTO);
	    } else {
	        // 기존 출하 건이 존재하면 해당 shipmentId를 사용
	        shipmentId = existingShipmentId;
	        shipmentDTO.setShipmentId(shipmentId);
	    }

	    // 3) 출하 상세 품목 처리 (신규이든, 기존이든 공통 로직)
	    List<ShipmentDetailDTO> details = shipmentDTO.getItems();
	    if (details == null || details.isEmpty()) {
	        throw new IllegalArgumentException("출하할 품목이 없습니다.");
	    }

	    boolean allItemsShipped = true;

	    for (ShipmentDetailDTO detail : details) {
	        detail.setShipmentId(shipmentId); // 현재 처리 중인 shipmentId 설정
	        detail.setOrderId(shipmentDTO.getOrderId());
	        
	        // 기존 출하 상세 기록이 있는지 확인 (출하번호 + 품목번호)
	        ShipmentDetailDTO existingDetail = shipmentMapper.getShipmentDetailByShipmentAndProduct(shipmentId, detail.getProductId());
	        
	        // 기존 출하기록이 있고, 진행상태가 부분출하인 경우(=부분출하인 품목을 추가로 출하할 경우)에는 출하 수량을 1개이상 입력
	        if (existingDetail != null && "PARTIAL".equals(existingDetail.getShipmentDetailStatus())) {
	        	
	            // 이번에 입력한 출하 수량(getShipmentQty())이 1개 미만일 경우 예외 발생
	            if (detail.getShipmentQty() <= 0) {
	                throw new IllegalArgumentException("부분출하 품목은 출하 수량을 1개 이상 입력해야 합니다.");
	            }
	        }
	        
	        // 새로운 출하 수량 (기존 출하수량 + 이번에 출하할 수량)
	        int newTotalQty = detail.getShipmentQty();
	        if (existingDetail != null) {
	            newTotalQty += existingDetail.getShipmentQty();
	        }

	        // 출하 수량이 0보다 클 경우에만 재고 차감(이번에 출하할 수량만 차감)
	        if (detail.getShipmentQty() > 0) {
	            deductStock(detail.getProductId(), detail.getOrderId() ,detail.getShipmentQty());
	        }
	        
	        // 출하수량에 따른 상태 결정
	        String shipmentDetailStatus;
	        if (newTotalQty == 0) { // 출하 수량이 0일 경우
	        	shipmentDetailStatus = ("READY");
	            allItemsShipped = false;
	        } else if (newTotalQty < detail.getOrderQty()) { 
	        	shipmentDetailStatus = ("PARTIAL");
	            allItemsShipped = false;
	        } else {
	        	shipmentDetailStatus = ("COMPLETION");
	        }
	        
	        detail.setShipmentDetailStatus(shipmentDetailStatus);
	        
	        if (existingDetail != null) {
	            // 기존 기록이 있다면, 출하 수량 및 상태 업데이트
	        	detail.setId(existingDetail.getId()); // ID 설정
	            detail.setShipmentQty(newTotalQty); // 총 출하 수량으로 설정
	            shipmentMapper.updateShipmentDetail(detail);
	        } else {
	            // 기존 기록이 없다면, 새로운 shipment_detail INSERT
	            int seq = shipmentMapper.getNextDetailId(shipmentId);
	            detail.setId(seq);
	            shipmentMapper.insertShipmentDetail(detail);
	        }
	        
	        String orderDetailStatusToUpdate;
	        if ("COMPLETION".equals(shipmentDetailStatus)) {
	            orderDetailStatusToUpdate = "COMPLETION";
	        } else {
	            orderDetailStatusToUpdate = "INSHIPMENT"; // '출하진행중' 상태로 업데이트
	        }
	        shipmentMapper.updateOrderDetailStatus(shipmentDTO.getOrderId(), detail.getProductId(), orderDetailStatusToUpdate);
	        
	    }
	    
	    // 4) 전체 출하 및 수주 상태 최종 업데이트
	    if (allItemsShipped) {
	        shipmentMapper.updateShipmentStatus(shipmentId);
	        shipmentMapper.updateOrderStatus(shipmentDTO.getOrderId(), "COMPLETION");
	    } else {
	        shipmentMapper.updateShipmentStatusPartial(shipmentId); // '부분출하'로 업데이트하는 별도 쿼리 필요
	        shipmentMapper.updateOrderStatus(shipmentDTO.getOrderId(), "INSHIPMENT"); // 수주 상태를 '출하진행중'으로 업데이트
	    }
	    
	    // 모든 품목의 출하 수량이 0이면(즉, 미출하(NOTSHIPPED) 상태이면), 출하 상태를 출하대기(READY)로 변경
	    shipmentMapper.updateShipmentStatusToReadyIfAllNotShipped(shipmentId);
	    
	    return shipmentId;
	}
	
	private void deductStock(String productId, String orderId, int shipmentQty) {
        List<OutputDTO> outputItems = shipmentMapper.getOutputItemsByProductIdAndOrderId(productId, orderId);
        
        int remainingQtyToDeduct = shipmentQty;
        
        for (OutputDTO item : outputItems) {
            if (remainingQtyToDeduct <= 0) {
                break;
            }
            
            int currentStock = item.getOutCount();
            int deductAmount;
            
            if (currentStock >= remainingQtyToDeduct) {
                deductAmount = remainingQtyToDeduct;
                remainingQtyToDeduct = 0;
            } else {
                deductAmount = currentStock;
                remainingQtyToDeduct -= currentStock;
            }
            
            // 1. 출고등록시 출고대기 상태가되며 output 테이블에 insert됨
            // 2. 출고등록한 수량이 out_count값이고 등록 수량만큼 창고에서 이미 재고가 빠졌기 때문에
            //    해당 product_id의 창고 재고수량을 update 하는게 아닌 output 테이블의 out_count값을 출하수량만큼 차감
            shipmentMapper.updateOutputItemCount(item.getProductId(), item.getOrderId(), deductAmount);
        }
        
        if (remainingQtyToDeduct > 0) {
            throw new IllegalArgumentException("재고가 부족합니다. 출하하려는 수량만큼의 재고를 확보할 수 없습니다.");
        }
    }	
	
	// 모든 출하 목록
	public List<ShipmentDTO> getAllShipment() {
		
		return shipmentMapper.getAllShipment();
	}
	
	// 선택한 출하에 대한 상세 목록
	public List<ShipmentDetailDTO> getShipmentDetailsByShipmentId(String shipmentId) {
		
		return shipmentMapper.getShipmentDetailsByShipmentId(shipmentId);
	}
	
	// 수주별 남은 출하 대상 품목만 조회
	public List<ShipmentDetailDTO> getPendingShipmentDetails(String orderId) {
	    return shipmentMapper.getPendingShipmentDetails(orderId);
	}
	
	// 날짜 지연 로직
	@Transactional
	public void updateDelayedShipments() {
	    shipmentMapper.updateShipmentStatusToDelay();
	    shipmentMapper.updateShipmentDetailStatusToDelay();
	    // 이 메서드는 스케줄러에 등록하여 매일 자정에 실행되어야 하는데 안되는중,,0924 확인
	}
	
}
