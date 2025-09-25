package com.erp_mes.mes.stock.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.erp_mes.mes.stock.dto.MaterialDTO;
import com.erp_mes.mes.stock.dto.ProductDTO;
import com.erp_mes.mes.stock.dto.StockDTO;
import com.erp_mes.mes.stock.dto.WarehouseDTO;
import com.erp_mes.mes.stock.service.StockService;
import com.erp_mes.mes.stock.service.WareService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2  
@Controller
@RequiredArgsConstructor
public class InvController {
    
    private final StockService stockService;
    private final WareService wareService;
    
    // ==================== 1. 페이지 라우팅 ====================
    
     //재고 현황 페이지
    @GetMapping("/inventory/stock")
    public String stockList(Model model) {
        log.info("재고 현황 페이지 접속");
        List<WarehouseDTO> warehouseList = stockService.getWarehouseList();
        model.addAttribute("warehouseList", warehouseList);
        return "inventory/stock_list";
    }
    
    //입고 관리 페이지
    @GetMapping("/purchase/goods")
    public String inboundList(Model model) {
        log.info("입고 관리 페이지 접속");
        return "inventory/inbound_list";
    }
    
    //출고 관리 페이지
    @GetMapping("/inventory/outbound")
    public String outboundList(Model model) {
        log.info("출고 관리 페이지 접속");
        return "inventory/outbound_list";
    }
    
     //자재(부품/반제품) 관리 페이지
    @GetMapping("/inventory/material")
    public String materialList(Model model) {
        log.info("자재 관리 페이지 접속");
        return "inventory/material_list";
    }
    
    // 제품(완제품) 관리 페이지 (프로젝트에선 구현X - 담당자 따로있음.)
    @GetMapping("/inventory/item")
    public String itemList(Model model) {
        log.info("제품 관리 페이지 접속");
        return "inventory/item_list";
    }
    
    // ==================== 2. 재고 현황 관리 API ====================
   
    //전체 재고 목록 조회 (완제품, 반제품, 부품)
    @GetMapping("/api/inventory/all-stock")
    @ResponseBody
    public List<StockDTO> getAllStockList(
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "warehouseId", required = false) String warehouseId) {
        
        log.info("전체 재고 조회 - 품목명: {}, 창고: {}", productName, warehouseId);
        return stockService.getAllStockList(productName, warehouseId);
    }
    
    // 재고 상세 조회
    @GetMapping("/api/inventory/stock/{productId}")
    @ResponseBody
    public StockDTO getStockDetail(@PathVariable String productId) {
        log.info("재고 상세 조회 - 품목ID: {}", productId);
        return stockService.getStockDetail(productId);
    }
    
    // 재고수량 수정
    @PostMapping("/api/inventory/stock/update")
    @ResponseBody
    public Map<String, Object> updateStock(
            @RequestParam String productId,
            @RequestParam String warehouseId,
            @RequestParam Integer itemAmount) {
        
        log.info("재고 수량 수정 - 품목: {}, 창고: {}, 수량: {}", 
                productId, warehouseId, itemAmount);
        
        Map<String, Object> result = new HashMap<>();
        boolean success = stockService.updateStockAmount(productId, warehouseId, itemAmount);
        
        result.put("success", success);
        result.put("message", success ? "재고 수량이 수정되었습니다." : "재고 수량 수정에 실패했습니다.");
        return result;
    }
    
    // 창고별 재고 조정 (증가/차감)
    @PostMapping("/api/inventory/adjust-stock")
    @ResponseBody
    public Map<String, Object> adjustWarehouseStock(
            @RequestParam("productId") String productId,
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
    
    // ==================== 3. 자재(Material) 관리 API ====================
    
    // 자재 목록 조회
    @GetMapping("/api/inventory/materials")
    @ResponseBody
    public List<MaterialDTO> getMaterialList(
            @RequestParam(name = "materialType", required = false) String materialType,
            @RequestParam(name = "searchKeyword", required = false) String searchKeyword) {
        log.info("자재 목록 조회 - 구분: {}, 검색어: {}", materialType, searchKeyword);
        return stockService.getMaterialList(materialType, searchKeyword);
    }
    
    // 자재 등록
    @PostMapping("/api/inventory/materials")
    @ResponseBody
    public Map<String, Object> addMaterial(@RequestBody MaterialDTO dto) {
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
    
    // 자재 정보 수정
    @PutMapping("/api/inventory/materials/{materialId}")
    @ResponseBody
    public Map<String, Object> updateMaterial(
            @PathVariable("materialId") String materialId,
            @RequestBody MaterialDTO dto,
            Principal principal) {
        
        Map<String, Object> result = new HashMap<>();
        dto.setMaterialId(materialId);
        
        boolean success = stockService.updateMaterial(dto, principal.getName());
        result.put("success", success);
        return result;
    }
    
    // 자재 삭제 (다중선택 가능)
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
    
    // 자재 재고 차감 (생산 투입용)
    @PostMapping("/api/inventory/material/reduce")
    @ResponseBody
    public Map<String, Object> reduceMaterialStock(
            @RequestParam("materialId") String materialId,
            @RequestParam("warehouseId") String warehouseId,
            @RequestParam("locationId") String locationId,
            @RequestParam("reduceQty") Integer reduceQty,
            @RequestParam("reason") String reason,
            Principal principal) {
        
        log.info("재고 투입 - materialId: {}, warehouseId: {}, 수량: {}", 
                 materialId, warehouseId, reduceQty);
        
        Map<String, Object> result = new HashMap<>();
        try {
            // StockService 대신 WareService 호출
            boolean success = wareService.reduceMtlStock(
                materialId, warehouseId, locationId, reduceQty, reason, principal.getName()
            );
            result.put("success", success);
            result.put("message", success ? "재고 투입 완료" : "재고 투입 실패");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    // 자재의 창고별 재고 현황 조회
    @GetMapping("/api/inventory/material-warehouse-stock/{materialId}")
    @ResponseBody
    public List<Map<String, Object>> getMaterialWarehouseStock(@PathVariable("materialId") String materialId) {
        return stockService.getMaterialWarehouseStock(materialId);
    }

    // ==================== 4. 완제품(Product) 관리 API -- 담당자가 따로있어 개인용 포폴용 ====================
    
    // 완제품 목록 조회
    @GetMapping("/api/inventory/products")
    @ResponseBody
    public List<ProductDTO> getProductList(
            @RequestParam(name = "productType", required = false) String productType,
            @RequestParam(name = "searchKeyword", required = false) String searchKeyword) {
        return stockService.getProductList(productType, searchKeyword);
    }
    
    // 완제품 등록
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
    
    // 제품 정보 수정
    @PutMapping("/api/inventory/products/{productId}")
    @ResponseBody
    public Map<String, Object> updateProduct(
            @PathVariable("productId") String productId,
            @RequestBody ProductDTO dto,
            Principal principal) {
        
        Map<String, Object> result = new HashMap<>();
        dto.setProductId(productId);
        dto.setEmpId(principal.getName());
        
        boolean success = stockService.updateProduct(dto);
        result.put("success", success);
        return result;
    }
    
    // 완제품 삭제 (다중 선택 가능)
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
    
    // 제품의 창고별 재고 현황 조회
    @GetMapping("/api/inventory/warehouse-stock/{productId}")
    @ResponseBody
    public List<Map<String, Object>> getWarehouseStock(@PathVariable("productId") String productId) {
        return stockService.getWarehouseStockByProduct(productId);
    }
    
    // ==================== 5. 공통 코드 및 유틸리티 API ====================
    
    // 현재 로그인한 사용자 정보 조회
    @GetMapping("/api/current-user")
    @ResponseBody
    public Map<String, String> getCurrentUser(Principal principal) {
        Map<String, String> result = new HashMap<>();
        String empId = principal.getName();
        
        result.put("empId", empId);
        result.put("empName", stockService.getEmployeeName(empId));
        return result;
    }
    
    // 직원 목록 조회
    @GetMapping("/api/employees")
    @ResponseBody
    public List<Map<String, String>> getEmployeeList() {
        return stockService.getEmployeeList();
    }
    
    // 자재 타입 공통코드 조회
    @GetMapping("/api/common-codes/material-types")
    @ResponseBody
    public List<Map<String, String>> getMaterialTypes() {
        log.info("자재타입 공통코드 조회");
        return stockService.getMaterialTypes();
    }
    
    // 검사방법 목록 조회
    @GetMapping("/api/inventory/inspection-methods")
    @ResponseBody
    public List<Map<String, Object>> getInspectionMethods() {
        return stockService.getInspectionMethods();
    }
    
    // 단위 공통코드 조회
    @GetMapping("/api/inventory/units")
    @ResponseBody
    public List<Map<String, Object>> getUnitList() {
        return stockService.getUnitList();
    }
    
    // ==================== 6. 보류중  ====================
    
    // 재고목록 조회인데 일단 보류(legacy)
    @GetMapping("/api/inventory/stock")
    @ResponseBody
    public List<StockDTO> getStockList(
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "warehouseId", required = false) String warehouseId) {
        
        log.info("재고 조회 - 품목명: {}, 창고: {}", productName, warehouseId);
        return stockService.getStockList(productName, warehouseId);
    }
}