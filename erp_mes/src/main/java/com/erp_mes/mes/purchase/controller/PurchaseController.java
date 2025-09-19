package com.erp_mes.mes.purchase.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.erp.personnel.dto.PersonnelLoginDTO;
import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.pm.dto.ProductDTO;
import com.erp_mes.mes.purchase.dto.PurchaseDTO;
import com.erp_mes.mes.purchase.dto.PurchaseDetailDTO;
import com.erp_mes.mes.purchase.service.PurchaseService;
import com.erp_mes.mes.stock.dto.MaterialDTO;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/purchase")
@Log4j2
public class PurchaseController {
	private final PurchaseService purchaseService;

	public PurchaseController(PurchaseService purchaseService) {
		this.purchaseService = purchaseService;
	}

	// 수주 화면
	@GetMapping("purchaseOrder")
	public String order() {

		return "/purchase/purchaseOrder";
	}

	// 발주 등록
	@PostMapping("api/purchase/submit")
	public ResponseEntity<?> submitOrder(@RequestBody PurchaseDTO purchaseDTO, @AuthenticationPrincipal PersonnelLoginDTO userDetails) {
		try {
			// 로그인 사용자 정보 세팅
			purchaseDTO.setEmpId(userDetails.getEmpId());
			//purchaseDTO.setEmpName(userDetails.getName());

			// 서비스 호출
			String purchaseId = purchaseService.createPurchase(purchaseDTO);

			return ResponseEntity.ok(Map.of("purchaseId", purchaseId, "status", "success", "message", "발주 정상 등록"));

		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(Map.of("status", "fail", "error", ex.getMessage()));
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "error", "서버 오류: " + ex.getMessage()));
		}
	}
	
	// 자재 리스트
	@GetMapping("/api/materials")
	@ResponseBody
	public List<MaterialDTO> getAllMaterial() {
		log.info("자재 목록 조회 요청");

		return purchaseService.getAllMaterial();
	}

	// 발주 목록 조회
	@GetMapping("/api/purchase")
	@ResponseBody
	public List<PurchaseDTO> getAllPurchaseOrder() {
		log.info("발주 목록 조회 요청");
		
		return purchaseService.getAllPurchase();
	}
	
	// 발주 상세 목록 조회
	@GetMapping("/api/purchase/{purchaseId}/details")
	@ResponseBody
	public List<PurchaseDetailDTO> getPurchaseDetailsByOrderId(@PathVariable("purchaseId") String purchaseId) {
		log.info("발주 상세 목록 조회 요청, 발주 ID: {}", purchaseId);
		
		return purchaseService.getPurchaseDetailsByOrderId(purchaseId);
	}
	
//	// 발주 단건 조회 -> 발주 수정 모달창에서 발주 등록 때 입력했던 데이터값 가져오기 위해
//	@GetMapping("/api/purchase/{purchaseId}")
//	@ResponseBody
//	public ResponseEntity<?> getPurchase(@PathVariable("purchaseId") String purchaseId) {
//		PurchaseDTO purchase = purchaseService.getPurchaseById(purchaseId);
//		if (purchase == null) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND)
//					.body("해당 발주를 찾을 수 없습니다.");
//		}
//		return ResponseEntity.ok(purchase);
//	}
	
//	// 발주 수정
//	@PutMapping("/api/purchase/{purchaseId}")
//    public ResponseEntity<?> updateOrder(@PathVariable("purchaseId") String purchaseId, @RequestBody PurchaseDTO purchaseDTO) {
//    	log.info("updateOrder body: {}", purchaseDTO); 
//    	purchaseDTO.setPurchaseId(purchaseId);
//        
//     // items 안에도 orderId 세팅
//        if (purchaseDTO.getItems() != null) {
//            orderDTO.getItems().forEach(item -> item.setOrderId(orderId));
//        }
//        
//        orderService.updateOrder(orderDTO);
//        
//        return ResponseEntity.ok(Map.of("orderId", orderId, "message", "수주 수정이 완료되었습니다."));
//
//    }
	
}
