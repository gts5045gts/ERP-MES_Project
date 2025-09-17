package com.erp_mes.mes.business.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.service.BusinessService;

import lombok.extern.log4j.Log4j2;


@Controller
@RequestMapping("/business")
@Log4j2
public class BusinessController {
	
	private BusinessService businessService;
	
	public BusinessController(BusinessService businessService) {
		this.businessService = businessService;
	}
	
	// 수주 화면
	@GetMapping("order")
	public String order() {
			
		return "/business/order";
	}
	
	// 수주 목록 조회
    @GetMapping("/api/orders")
    @ResponseBody
    public List<OrderDTO> getAllOrder() {
        log.info("수주 목록 조회 요청");
        
        return businessService.getAllOrder();
    }
    
    // 수주 등록
    @PostMapping("/api/orders/submit")
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDto) {
        try {
            businessService.saveOrder(orderDto);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Order created successfully"));
        } catch (Exception e) {
            log.error("Order creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
    
//    @GetMapping("/api/products")
//    @ResponseBody
//    public List<ProductDTO> getAllProducts() {
//        // ProductService와 ProductMapper를 통해 product 테이블 조회
//        return productService.getAllProducts();
//    }
	
}
