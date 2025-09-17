package com.erp_mes.mes.business.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.mapper.BusinessMapper;
import com.erp_mes.mes.pm.dto.ProductDTO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class BusinessService {
	private final BusinessMapper businessMapper;
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyyMMdd");

	public BusinessService(BusinessMapper businessMapper) {
		this.businessMapper = businessMapper;
	}

	@Transactional
    public String createOrder(Map<String, Object> orderData) {
        // 1) clientName -> clientId
        String clientName = (String) orderData.get("clientName");
        String clientId = businessMapper.findClientIdByName(clientName);
        if (clientId == null) {
            throw new IllegalArgumentException("해당 거래처를 찾을 수 없습니다: " + clientName);
        }

        // 2) 수주번호 생성 (ORD-yyyyMMdd-XXXX)
        int count = businessMapper.countOrders();
        int next = count + 1;
        String datePart = LocalDate.now().format(dateFmt);
        String seqPart = String.format("%04d", next);
        String orderId = "ORD-" + datePart + "-" + seqPart;
        

        // 3) 주문 기본 정보 구성
        String empId = (String) orderData.get("empId");
        String empName = (String) orderData.get("empName");
        String deliveryDate = (String) orderData.get("deliveryDate"); 


        List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("품목이 선택되지 않았습니다.");
        }

        // 4) 총 수량, 총 금액 계산
        int totalQty = 0;
        int totalPrice = 0;
        for (Map<String, Object> it : items) {
            int qty = ((Number) it.getOrDefault("qty", 0)).intValue();
            long price = ((Number) it.getOrDefault("price", 0)).longValue();
            totalQty += qty;
            totalPrice += qty * price;
        }

        // 5) orders insert
        Map<String, Object> orderParams = Map.of(
                "orderId", orderId,
                "clientId", clientId,
                "clientName", clientName,
                "empId", empId,
                "empName", empName,
                "orderDate", java.sql.Date.valueOf(LocalDate.now()),
                "deliveryDate", (deliveryDate != null && !deliveryDate.isEmpty()) ?
                    	java.sql.Date.valueOf(LocalDate.parse(deliveryDate)) : null, // LocalDate로 변환하여 저장
                "orderQty", totalQty,
                "orderPrice", totalPrice
        );
        businessMapper.insertOrder(orderParams);

        // 6) orders_detail insert
        int seq = 1;
        for (Map<String, Object> it : items) {
            Map<String, Object> detailParams = Map.of(
                    "orderId", orderId,
                    "seqId", seq,
                    "productId", it.get("productId"),
                    "productName", it.get("productName"),
                    "unit", it.get("unit"),
                    "orderQty", ((Number) it.getOrDefault("qty", 0)).intValue(),
                    "orderPrice", ((Number) it.getOrDefault("price", 0)).intValue()
            );
            businessMapper.insertOrderDetail(detailParams);
            seq++;
        }

        return orderId;
    }
	
	// 수주 등록 모달에 보여줄 품목 리스트
	public List<ProductDTO> getAllProduct() {
			
		return businessMapper.getAllProduct();
	}
	
	// 수주 전체 목록
	public List<OrderDTO> getAllOrder() {
		return businessMapper.getAllOrder();
	}

	// 수주 상세 목록 리스트
	public List<OrderDetailDTO> getOrderDetailsByOrderId(String orderId) {
		
		return businessMapper.getOrderDetailsByOrderId(orderId);
	}
}
