package com.erp_mes.mes.business.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.mapper.OrderMapper;
import com.erp_mes.mes.pm.dto.ProductDTO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OrderService {
	private final OrderMapper orderMapper;
//    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyyMMdd");

	public OrderService(OrderMapper orderMapper) {
		this.orderMapper = orderMapper;
	}

	@Transactional
	public String createOrder(OrderDTO orderDTO) {
        // 1) 수주번호 생성 (ORD-yyyyMMdd-XXXX)
        // DTO를 사용하므로 매퍼를 통해 이미 계산된 총 수량, 금액을 가져올 수 있습니다.
        int count = orderMapper.countOrders();
        int next = count + 1;
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seqPart = String.format("%04d", next);
        String orderId = "ORD-" + datePart + "-" + seqPart;
        orderDTO.setOrderId(orderId);

        // 2) orders insert
        // DTO 객체를 직접 매퍼로 전달합니다.
        orderMapper.insertOrder(orderDTO);

        // orders_detail insert
        List<OrderDetailDTO> items = orderDTO.getItems();
        if (items != null && !items.isEmpty()) {
            int seq = 1;
            for (OrderDetailDTO item : items) {
                item.setOrderId(orderDTO.getOrderId());
                item.setId(seq); // 서비스 계층에서 직접 순번을 할당
                orderMapper.insertOrderDetail(item);
                seq++;
            }
        }
        return orderDTO.getOrderId();
    }
	
	// 수주 등록 모달에 보여줄 품목 리스트
	public List<ProductDTO> getAllProduct() {
			
		return orderMapper.getAllProduct();
	}
	
	// 수주 전체 목록
	public List<OrderDTO> getAllOrder() {
		return orderMapper.getAllOrder();
	}
	
	// 수주 수정 모달창에 기존 값 불러오기
	public OrderDTO getOrderById(String orderId) {
	    return orderMapper.getOrderById(orderId);
	}
	
	// 검색 조건에 따른 수주 목록 조회
	public List<OrderDTO> searchOrders(String orderStatus, String clientName) {
	    return orderMapper.searchOrders(orderStatus, clientName);
	}

	// 수주 상세 목록 리스트
	public List<OrderDetailDTO> getOrderDetailsByOrderId(String orderId) {
		
		return orderMapper.getOrderDetailsByOrderId(orderId);
	}

	// 수주 취소 처리
	@Transactional
    public void cancelOrder(String orderId) {
        String currentStatus = orderMapper.findOrderStatus(orderId);

        if ("CANCELED".equals(currentStatus)) {
            throw new IllegalArgumentException("이미 취소된 수주입니다.");
        }

        orderMapper.updateOrderStatus(orderId, "CANCELED");
    }
	
	@Transactional
    public void updateOrder(OrderDTO orderDTO) {
		
        // orders 테이블 업데이트
		orderMapper.updateOrder(orderDTO);
       
        // 기존 orders_detail 삭제 (전체 삭제 후 재등록)
		orderMapper.deleteOrderDetails(orderDTO.getOrderId());
        
        // 새로운 orders_detail 재등록
        List<OrderDetailDTO> items = orderDTO.getItems();
        if (items != null && !items.isEmpty()) {
             int seq = 1;
             for (OrderDetailDTO item : items) {
                 item.setOrderId(orderDTO.getOrderId());
                 item.setId(seq);
                 orderMapper.insertOrderDetail(item);
                 seq++;
             }
        }
    }
	
}
