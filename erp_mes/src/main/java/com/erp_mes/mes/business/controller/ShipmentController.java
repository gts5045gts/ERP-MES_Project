package com.erp_mes.mes.business.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.erp.personnel.dto.PersonnelLoginDTO;
import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.business.dto.ShipmentDTO;
import com.erp_mes.mes.business.dto.ShipmentDetailDTO;
import com.erp_mes.mes.business.service.ShipmentService;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/business")
@Log4j2
public class ShipmentController {

	private final ShipmentService shipmentService;

	public ShipmentController(ShipmentService shipmentService) {
		this.shipmentService = shipmentService;
	}

	@GetMapping("/shipment")
	public String shipment(Model model, @AuthenticationPrincipal PersonnelLoginDTO userDetails) {

		String userDeptId = userDetails.getEmpDeptId();
		String userLevelId = userDetails.getEmpLevelId();
		
		// 부서 코드가 'DEP006'(영업팀)일 경우, 버튼 표시 여부를 true로 설정
        boolean isBusTeam = "DEP006".equals(userDeptId);
        model.addAttribute("isBUSTeam", isBusTeam);
        
        boolean isAutLevel = "AUT001".equals(userLevelId);
        model.addAttribute("isAUTLevel", isAutLevel);
		
		return "business/shipment";
	}

	// 등록, 생산중인 수주 목록 조회
	@GetMapping("/api/shipment/orders")
	@ResponseBody
	public List<OrderDTO> getStatusOrder() {
		log.info("출하등록에서 수주 목록 조회 요청");

		return shipmentService.getStatusOrder();
	}

	// 선택한 수주의 상세 정보 조회
	@GetMapping("/api/shipment/ordersDetail")
	@ResponseBody
	public List<OrderDetailDTO> getOrderDetail(@RequestParam("orderId") String orderId) {
		return shipmentService.getOrderDetailWithStockAndStatus(orderId);
	}

	// 출하 등록 + 수정 
	@PostMapping("api/shipment/submit")
	public ResponseEntity<?> submitOrder(@RequestBody ShipmentDTO shipmentDTO, @AuthenticationPrincipal PersonnelLoginDTO userDetails) {
		try {
			// 로그인 사용자 정보 세팅
			shipmentDTO.setEmpId(userDetails.getEmpId());
			shipmentDTO.setEmpName(userDetails.getName());

			// 서비스 호출
			String shipmentId = shipmentService.createShipment(shipmentDTO);

			return ResponseEntity.ok(Map.of("shipmentId", shipmentId, "status", "success", "message", "출하가 정상적으로 등록되었습니다."));

		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(Map.of("status", "fail", "error", ex.getMessage()));
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "error", "서버 오류: " + ex.getMessage()));
		}
	}
	
	// 출하 목록 조회
	@GetMapping("/api/shipment")
	@ResponseBody
	public List<ShipmentDTO> getAllShipment() {
		log.info("출하 목록 조회 요청");
		
		return shipmentService.getAllShipment();
	}
	
	// 출하 상세 목록 조회
	@GetMapping("/api/shipment/{shipmentId}/details")
	@ResponseBody
	public List<ShipmentDetailDTO> getShipmentDetailsByShipmentId(@PathVariable("shipmentId") String shipmentId) {
		log.info("출하 상세 목록 조회 요청, 출하 ID: {}", shipmentId);
		
		return shipmentService.getShipmentDetailsByShipmentId(shipmentId);
	}
	
}
