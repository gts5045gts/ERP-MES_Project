package com.erp_mes.mes.business.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.business.dto.ShipmentDTO;
import com.erp_mes.mes.business.dto.ShipmentDetailDTO;
import com.erp_mes.mes.business.dto.WarehouseItemDTO;

@Mapper
public interface ShipmentMapper {
	
	// 등록, 생산중인 수주 목록 조회
	List<OrderDTO> getStatusOrder();

	// 출하 등록 모달창 -> 수주 목록에서 선택 -> 선택한 수주 id를 orders_detail 테이블에서 참조 -> product 테이블과 조인해서 재고량 가져옴
    List<OrderDetailDTO> getOrderDetailWithStockAndStatus(@Param("orderId") String orderId);
    
	// product_id로 재고가 많은 순서대로 warehouse_item 조회
    List<WarehouseItemDTO> getWarehouseItemsByProductId(@Param("productId") String productId);
    
	// warehouse_item 재고 수량 차감
    int updateWarehouseItemAmount(@Param("manageId") String manageId, @Param("amount") int amount);
    
    // 출하번호 부여를 위한 컬럼수 체크
	int countShipment();

	void insertShipment(ShipmentDTO shipmentDTO);

	void insertShipmentDetail(ShipmentDetailDTO order);

	// orderId로 주문 정보(clientId, deliveryDate) 조회
	OrderDTO getOrderInfoByOrderId(@Param("orderId") String orderId);
    
    List<ShipmentDTO> getAllShipment();
    
    List<ShipmentDetailDTO> getShipmentDetailsByShipmentId(@Param("shipmentId")String shipmentId);
    
    
    
 // 출하 상세 상태가 COMPLETION인 품목에 대해 수주 상세 상태를 업데이트
    void updateOrderDetailStatusFromShipment(@Param("shipmentId") String shipmentId);
    
    // 수주 상세 상태가 모두 COMPLETION이면, 수주 상태도 COMPLETION으로 업데이트
    void updateOrderStatusIfAllDetailsCompleted(@Param("orderId") String orderId);

	List<ShipmentDetailDTO> getPendingShipmentDetails(String orderId);

	int getNextDetailId(String shipmentId);
}
