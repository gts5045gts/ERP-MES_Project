package com.erp_mes.mes.stock.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.mes.stock.dto.WarehouseDTO;
import com.erp_mes.mes.stock.service.WareService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2  
@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class WareController {
	
//	private final InvService invService;
	private final WareService wareService;
	
	//창고내 재고 현황
	@GetMapping("/warehouse")
    public String warehouseList(Model model) {
        log.info("창고 현황 페이지 접속");
        return "inventory/warehouse_list";
    }
	
	//창고 정보 관리
	@GetMapping("/ware")
    public String wareList(Model model) {
        log.info("창고 현황 페이지 접속");
        return "inventory/ware_list";
    }
	
	// 창고 목록 조회
	@GetMapping("/api/warehouses")
	@ResponseBody
	public List<WarehouseDTO> getWarehouseList(
	        @RequestParam(name = "warehouseType", required = false) String warehouseType,
	        @RequestParam(name = "warehouseStatus", required = false) String warehouseStatus,
	        @RequestParam(name = "searchKeyword", required = false) String searchKeyword) {
	    
	    log.info("창고 목록 API 호출");
	    return wareService.getWarehouseList(warehouseType, warehouseStatus, searchKeyword);
	}
	
	// 창고 등록
	@PostMapping("/api/warehouses")
	@ResponseBody
	public Map<String, Object> addWarehouse(@RequestBody WarehouseDTO dto, Principal principal) {
	    Map<String, Object> result = new HashMap<>();
	    
	    try {
	        // 기본값 설정
	        if(dto.getWarehouseStatus() == null) {
	            dto.setWarehouseStatus("Y");
	        }
	        wareService.addWarehouse(dto);
	        result.put("success", true);
	        result.put("message", "창고가 등록되었습니다.");
	    } catch (Exception e) {
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    
	    return result;
	}
	
	// 창고 수정
	@PutMapping("/api/warehouses/{warehouseId}")
	@ResponseBody
	public Map<String, Object> updateWarehouse(
	        @PathVariable("warehouseId") String warehouseId,
	        @RequestBody WarehouseDTO dto,
	        Principal principal) {
	    
	    Map<String, Object> result = new HashMap<>();
	    dto.setWarehouseId(warehouseId);
	    // dto.setEmpId() 제거 - 담당자 변경 안 함
	    
	    boolean success = wareService.updateWarehouse(dto);
	    result.put("success", success);
	    result.put("message", success ? "수정 완료" : "수정 실패");
	    
	    return result;
	}
	
	// 창고 삭제
	@DeleteMapping("/api/warehouses")
	@ResponseBody
	public Map<String, Object> deleteWarehouses(@RequestBody List<String> ids) {
	    Map<String, Object> result = wareService.deleteWarehouses(ids);
	    
	    if(result.containsKey("failed")) {
	        List<String> failed = (List<String>) result.get("failed");
	        result.put("message", 
	            failed.size() + "개 창고는 재고가 있어 삭제할 수 없습니다.\n" +
	            "창고ID: " + String.join(", ", failed));
	    } else {
	        result.put("message", "삭제 완료");
	    }
	    return result;
	}
}
