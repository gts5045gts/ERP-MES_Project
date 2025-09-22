package com.erp_mes.mes.business.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.business.dto.ShipmentDTO;
import com.erp_mes.mes.business.dto.ShipmentDetailDTO;

@Mapper
public interface ShipmentMapper {
	
	// 등록, 생산중인 수주 목록 조회
	List<OrderDTO> getStatusOrder();

	// 출하 등록 모달창 -> 수주 목록에서 선택 -> 선택한 수주 id를 orders_detail 테이블에서 참조 -> product 테이블과 조인해서 재고량 가져옴
    List<OrderDetailDTO> getOrderDetailWithStock(@Param("orderId") String orderId);
    
    // 출하번호 부여를 위한 컬럼수 체크
	int countShipment();

	void insertShipment(ShipmentDTO shipmentDTO);

	void insertShipmentDetail(ShipmentDetailDTO order);

	// orderId로 주문 정보(clientId, deliveryDate) 조회
	OrderDTO getOrderInfoByOrderId(@Param("orderId") String orderId);
    
    // orderId와 productName으로 제품 정보(productId, orderQty) 조회
//    Map<String, Object> getProductInfoByOrderIdAndProductName(@Param("orderId") String orderId, @Param("productName") String productName);
    
    List<ShipmentDTO> getAllShipment();
    
    List<ShipmentDetailDTO> getShipmentDetailsByShipmentId(@Param("shipmentId")String shipmentId);
	
}
