package com.erp_mes.mes.stock.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.mes.stock.dto.ProductDTO;
import com.erp_mes.mes.stock.dto.StockDTO;
import com.erp_mes.mes.stock.dto.WarehouseDTO;
import com.erp_mes.mes.stock.service.StockService;
import com.erp_mes.mes.stock.service.WareService;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;  
@Log4j2  
@Controller
@RequiredArgsConstructor
public class InvController {
    
	private final WareService wareService;
	private final StockService stockService;
	
	// 재고 현황 페이지
    @GetMapping("/inventory/stock")
    public String stockList(Model model) {
        log.info("재고 현황 페이지 접속");
        
        // 창고 목록 조회
        List<WarehouseDTO> warehouseList = stockService.getWarehouseList();
        model.addAttribute("warehouseList", warehouseList);
        
        return "inventory/stock_list";
    }
    
    // 재고 목록 조회 (Ajax)
    @GetMapping("/api/inventory/stock")
    @ResponseBody
    public List<StockDTO> getStockList(
    		@RequestParam(name = "productName", required = false) String productName,
    		@RequestParam(name = "warehouseId", required = false) String warehouseId) {
        
    	log.info("========== 재고 API 호출됨 ==========");  // 이거 추가!
        log.info("productName: {}, warehouseId: {}", productName, warehouseId);
        
        List<StockDTO> stockList = stockService.getStockList(productName, warehouseId);
        log.info("조회 결과 개수: {}", stockList.size());
        
        return stockList;
    }
    
    // 재고 상세 조회 (Ajax)
    @GetMapping("/api/inventory/stock/{productId}")
    @ResponseBody
    public StockDTO getStockDetail(@PathVariable String productId) {
        log.info("재고 상세 조회 API - 품목ID: {}", productId);
        return stockService.getStockDetail(productId);
    }
    
    // 재고 수량 수정 (Ajax)
    @PostMapping("/api/inventory/stock/update")
    @ResponseBody
    public Map<String, Object> updateStock(
            @RequestParam String productId,
            @RequestParam String warehouseId,
            @RequestParam Integer itemAmount) {
        
        log.info("재고 수량 수정 API - 품목ID: {}, 창고ID: {}, 수량: {}", 
                productId, warehouseId, itemAmount);
        
        Map<String, Object> result = new HashMap<>();
        boolean success = stockService.updateStockAmount(productId, warehouseId, itemAmount);
        
        result.put("success", success);
        result.put("message", success ? "재고 수량이 수정되었습니다." : "재고 수량 수정에 실패했습니다.");
        
        return result;
    }
    
    
	
	// 입고 관리
	@GetMapping("/purchase/goods")
	public String inboundList(Model model) {
	    log.info("입고 관리 페이지 접속");
	    return "inventory/inbound_list";
	}
	
	// 출고 관리
	@GetMapping("/inventory/outbound")
	public String outboundList(Model model) {
	    log.info("출고 관리 페이지 접속");
	    return "inventory/outbound_list";
	}
	
	// 0915 기준정보관리 - 자재 목록 페이지
	@GetMapping("/inventory/material")
	public String materialList(Model model) {
	    log.info("자재 관리 페이지 접속");
	    return "inventory/material_list";
	}
	
	// 기준정보관리 - 자재(부품) 목록 조회 API
	@GetMapping("/api/inventory/materials")
	@ResponseBody
	public List<ProductDTO> getMaterialList(
	        @RequestParam(name = "productType", required = false) String productType,  
	        @RequestParam(name = "searchKeyword", required = false) String searchKeyword) {  
	    log.info("자재 목록 조회 - 구분: {}, 검색어: {}", productType, searchKeyword);
	    return stockService.getMaterialList(productType, searchKeyword);
	}

	// 기준정보관리 - 자재(부품) 등록
	@PostMapping("/api/inventory/materials")
	@ResponseBody
	public Map<String, Object> addMaterial(@RequestBody ProductDTO dto) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        stockService.addMaterial(dto);
	        result.put("success", true);
	        result.put("message", "자재가 등록되었습니다.");
	    } catch (Exception e) {
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}

	// 기준정보관리 - 자재(부품) 수정
	@PutMapping("/api/inventory/materials/{productId}")
	@ResponseBody
	public Map<String, Object> updateMaterial(
	        @PathVariable("productId") String productId, 
	        @RequestBody ProductDTO dto,
	        Principal principal) {  // 현재 로그인 사용자 정보
	    
	    Map<String, Object> result = new HashMap<>();
	    dto.setProductId(productId);
	    
	    String modifierId = principal.getName();  // 수정자 ID
	    boolean success = stockService.updateMaterial(dto, modifierId);
	    
	    result.put("success", success);
	    return result;
	}

	// 기준정보관리 - 자재(부품) 삭제
	@DeleteMapping("/api/inventory/materials")
	@ResponseBody
	public Map<String, Object> deleteMaterials(@RequestBody List<String> ids) {
	    Map<String, Object> result = stockService.deleteMaterials(ids);
	    
	    if(result.containsKey("failed")) {
	        List<String> failed = (List<String>) result.get("failed");
	        result.put("message", 
	            failed.size() + "개 항목은 최근 1개월 내 입출고 내역이 있어 삭제할 수 없습니다.\n" +
	            "자재코드: " + String.join(", ", failed));
	    } else {
	        result.put("message", "삭제 완료");
	    }
	    
	    return result;
	}
	
	@GetMapping("/api/current-user")
    @ResponseBody
    public Map<String, String> getCurrentUser(Principal principal) {
        Map<String, String> result = new HashMap<>();
        String empId = principal.getName();  // 로그인한 사용자 ID
        
        // 사용자 정보 조회 (PersonnelService 사용)
        // 또는 간단하게 empId만 반환해도 됨
        result.put("empId", empId);
        result.put("empName", stockService.getEmployeeName(empId));
        
        return result;
    }
	
	// 기준정보관리 - 제품정보(완제품/반제품)
	@GetMapping("/inventory/item")
	public String itemList(Model model) {
	    log.info("출고 관리 페이지 접속");
	    return "inventory/item_list";
	}
	
	@GetMapping("/api/inventory/products") 
	@ResponseBody
	public List<ProductDTO> getProductList(
	        @RequestParam(name = "productType", required = false) String productType,
	        @RequestParam(name = "searchKeyword", required = false) String searchKeyword) {
	    return stockService.getProductList(productType, searchKeyword);
	}

	// 제품 등록
	@PostMapping("/api/inventory/products")
	@ResponseBody
	public Map<String, Object> addProduct(@RequestBody ProductDTO dto, Principal principal) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        dto.setEmpId(principal.getName());
	        stockService.addProduct(dto);
	        result.put("success", true);
	    } catch (Exception e) {
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}

	// 제품 수정
	@PutMapping("/api/inventory/products/{productId}")
	@ResponseBody
	public Map<String, Object> updateProduct(
	        @PathVariable("productId") String productId,  // 이름 명시
	        @RequestBody ProductDTO dto,
	        Principal principal) {
	    Map<String, Object> result = new HashMap<>();
	    dto.setProductId(productId);
	    dto.setEmpId(principal.getName());
	    
	    boolean success = stockService.updateProduct(dto);
	    result.put("success", success);
	    return result;
	}

	// 제품 삭제
	@DeleteMapping("/api/inventory/products")
	@ResponseBody
	public Map<String, Object> deleteProducts(@RequestBody List<String> ids) {
	    Map<String, Object> result = stockService.deleteProducts(ids);
	    
	    if(result.containsKey("failed")) {
	        List<String> failed = (List<String>) result.get("failed");
	        result.put("message", 
	            failed.size() + "개 항목은 최근 1개월 내 입출고 내역이 있어 삭제할 수 없습니다.");
	    } else {
	        result.put("message", "삭제 완료");
	    }
	    
	    return result;
	}
	
	// 직원 목록 조회 API
	@GetMapping("/api/employees")
	@ResponseBody
	public List<Map<String, String>> getEmployeeList() {
	    return stockService.getEmployeeList();
	}
	
	// 0916 특정 제품의 창고별 재고 조회 API
	@GetMapping("/api/inventory/warehouse-stock/{productId}")
	@ResponseBody
	public List<Map<String, Object>> getWarehouseStock(@PathVariable("productId") String productId) {  // "productId" 명시!
	    return stockService.getWarehouseStockByProduct(productId);
	}
	
	// 0916 창고별 재고 조정
	@PostMapping("/api/inventory/adjust-stock")
	@ResponseBody
	public Map<String, Object> adjustWarehouseStock(@RequestParam("productId") String productId,      
	        @RequestParam("warehouseId") String warehouseId,  
	        @RequestParam("adjustQty") Integer adjustQty,  
	        @RequestParam("adjustType") String adjustType,  
	        @RequestParam(value = "reason", required = false) String reason,
	        Principal principal) {
	    
	    Map<String, Object> result = new HashMap<>();
	    try {
	        boolean success = stockService.adjustWarehouseStock(
	            productId, warehouseId, adjustQty, adjustType, reason, principal.getName()
	        );
	        result.put("success", success);
	        result.put("message", success ? "재고 조정 완료" : "재고 조정 실패");
	    } catch (Exception e) {
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}
}