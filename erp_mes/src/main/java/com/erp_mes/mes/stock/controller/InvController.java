package com.erp_mes.mes.stock.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.mes.stock.dto.InvDTO;
import com.erp_mes.mes.stock.dto.StockDTO;
import com.erp_mes.mes.stock.dto.WarehouseDTO;
import com.erp_mes.mes.stock.service.InvService;
import com.erp_mes.mes.stock.service.StockService;
import com.erp_mes.mes.stock.service.WareService;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;  
@Log4j2  
@Controller
@RequiredArgsConstructor
public class InvController {
    
	private final InvService invService;
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
	
	// 기준정보관리 - 자재정보(소재)
	@GetMapping("/inventory/material")
	public String materialList(Model model) {
	    log.info("출고 관리 페이지 접속");
	    return "inventory/material_list";
	}
	
	// 기준정보관리 - 제품정보(완제품/반제품)
	@GetMapping("/inventory/item")
	public String itemList(Model model) {
	    log.info("출고 관리 페이지 접속");
	    return "inventory/item_list";
	}
}
