package com.erp_mes.mes.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.pm.dto.ProductDTO;

@Mapper
public interface OrderMapper {
	// 수주번호 부여를 위한 orders 테이블 컬럼수 체크
	int countOrders();
	
	// 수주 등록 시 선택한 clientName값으로 client_id값 찾음
	String findClientIdByName(@Param("clientName") String clientName);

    int insertOrder(OrderDTO orderDTO);

    int insertOrderDetail(OrderDetailDTO detailDTO);
	
    // 품목 리스트
 	List<ProductDTO> getAllProduct();
	
 	// 수주 목록
	List<OrderDTO> getAllOrder();
	
	// 수주 수정 모달창에 기존 값 불러오기
	OrderDTO getOrderById(String orderId);
	
	// 검색조건 수주 조회
	List<OrderDTO> searchOrders(@Param("orderStatus") String orderStatus, @Param("clientName") String clientName);
	
	// 수주 상세 목록
	List<OrderDetailDTO> getOrderDetailsByOrderId(@Param("orderId")String orderId);

// 수주 상태 취소, update====
	// 수주 상태 조회
	String findOrderStatus(String orderId);
    void updateOrderStatus(@Param("orderId") String orderId, @Param("orderStatus") String orderStatus);
    void updateOrderDetailsStatus(@Param("orderId") String orderId, @Param("orderStatus") String orderStatus);
 //==================   
    
// 수주 수정 update =======    
    void updateOrder(OrderDTO orderDTO);  
    void deleteOrderDetails(String orderId);
    // 한번의 수행에 여러 건의 OrderDetailDTO insert 
    void insertOrderDetails(List<OrderDetailDTO> items);
// ==================
   
    int updateOrderDetailStatus(@Param("orderId") String orderId, @Param("productId") String productId);

	void updateReason(@Param("orderId") String orderId, @Param("reason") String reason);


}
