package com.erp_mes.mes.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.business.dto.OutputDTO;
import com.erp_mes.mes.business.dto.ShipmentDTO;
import com.erp_mes.mes.business.dto.ShipmentDetailDTO;
import com.erp_mes.mes.stock.dto.WarehouseItemDTO;

@Mapper
public interface ShipmentMapper {
	
	// 등록, 생산중, 출하진행중(=부분출하)인 수주 목록 조회
	List<OrderDTO> getStatusOrder();

	// 출하 등록 모달창 -> 수주 목록에서 선택 -> 선택한 수주 id를 orders_detail 테이블에서 참조 -> product 테이블과 조인해서 재고량 가져옴
    List<OrderDetailDTO> getOrderDetailWithStockAndStatus(@Param("orderId") String orderId);
    
	// product_id로 재고가 많은 순서대로 warehouse_item 조회
    List<OutputDTO> getOutputItemsByProductIdAndOrderId(@Param("productId") String productId, @Param("orderId") String orderId);
    
	// warehouse_item 재고 수량 차감
    int updateOutputItemCount(@Param("productId") String productId, @Param("orderId") String orderId ,@Param("count") int count);
    
    // 출하번호 부여를 위한 컬럼수 체크
	int countShipment();

	void insertShipment(ShipmentDTO shipmentDTO);

	void insertShipmentDetail(ShipmentDetailDTO detail);

	// orderId로 주문 정보(clientId, deliveryDate) 조회
	OrderDTO getOrderInfoByOrderId(@Param("orderId") String orderId);
    
    List<ShipmentDTO> getAllShipment();
    
    List<ShipmentDetailDTO> getShipmentDetailsByShipmentId(@Param("shipmentId")String shipmentId);
    

	List<ShipmentDetailDTO> getPendingShipmentDetails(String orderId);

	int getNextDetailId(@Param("shipmentId")String shipmentId);
	
	
	// orderId를 사용하여 기존 shipmentId를 찾는 메서드
	String findExistingShipmentIdByOrderId(@Param("orderId") String orderId);

	// 출하번호(shipmentId)와 품목번호(productId)로 상세 품목을 찾는 메서드
	ShipmentDetailDTO getShipmentDetailByShipmentAndProduct(@Param("shipmentId") String shipmentId, @Param("productId") String productId);

	// 기존 shipment_detail 업데이트
	void updateShipmentDetail(ShipmentDetailDTO detail);

	// 출하 건의 전체 상태를 'COMPLETION'으로 업데이트
	void updateShipmentStatus(@Param("shipmentId") String shipmentId);

	// 출하 건의 전체 상태를 'PARTIAL'로 업데이트
	void updateShipmentStatusPartial(@Param("shipmentId") String shipmentId);
	
	// 특정 수주 상세의 상태를 업데이트
    void updateOrderDetailStatus(@Param("orderId") String orderId, @Param("productId") String productId, @Param("status") String status);
    
    // 수주 전체의 상태를 업데이트
    void updateOrderStatus(@Param("orderId") String orderId, @Param("status") String status);
    
    // 날짜 지연 상태로 출하를 업데이트하는 메서드
    void updateShipmentStatusToDelay();

    // 날짜 지연된 출하의 상세 상태를 업데이트하는 메서드
    void updateShipmentDetailStatusToDelay();
    
    // 지연된 품목 ID 목록을 조회하는 메서드
    List<String> getDelayedProductIds();
    
    void updateShipmentStatusToReadyIfAllNotShipped(String shipmentId);
}
