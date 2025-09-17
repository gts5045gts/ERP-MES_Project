package com.erp_mes.mes.business.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.erp.personnel.dto.PersonnelLoginDTO;
import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.business.service.BusinessService;
import com.erp_mes.mes.pm.dto.ProductDTO;

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
	
	// 수주 등록
	@PostMapping("api/orders/submit")
	public ResponseEntity<?> submitOrder(@RequestBody Map<String, Object> payload, @AuthenticationPrincipal PersonnelLoginDTO userDetails) {
		log.info("clientName: " + payload.get("clientName"));
		try {
			// 로그인 사용자 정보 세팅
			payload.put("empId", userDetails.getEmpId());
			payload.put("empName", userDetails.getName());

			// 서비스 호출
			String orderId = businessService.createOrder(payload);

			return ResponseEntity.ok(Map.of("orderId", orderId, "status", "success", "message", "주문이 정상적으로 등록되었습니다."));

		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(Map.of("status", "fail", "error", ex.getMessage()));
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "error", "서버 오류: " + ex.getMessage()));
		}
	}

	// ------------------------------------------

	// 수주 목록 조회
	@GetMapping("/api/orders")
	@ResponseBody
	public List<OrderDTO> getAllOrder() {
		log.info("수주 목록 조회 요청");

		return businessService.getAllOrder();
	}

	// 수주 상세 목록 조회
	@GetMapping("/api/orders/{orderId}/details")
	@ResponseBody
	public List<OrderDetailDTO> getOrderDetailsByOrderId(@PathVariable("orderId") String orderId) {
		log.info("수주 상세 목록 조회 요청, 수주 ID: {}", orderId);
		return businessService.getOrderDetailsByOrderId(orderId);
	}


	@GetMapping("/api/products")
	@ResponseBody
	public List<ProductDTO> getAllProduct() {
		log.info("품목 목록 조회 요청");

		return businessService.getAllProduct();
	}
	
	// 수주 취소 
    @PutMapping("/api/orders/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable("orderId") String orderId) {
        try {
            businessService.cancelOrder(orderId);
            return ResponseEntity.ok(Map.of("orderId", orderId, "newStatus", "CANCELED"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("수주 취소 실패");
        }
    }
	
}
