package com.erp_mes.mes.stock.service;

import java.util.ArrayList; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.stock.dto.ProductDTO;
import com.erp_mes.mes.stock.dto.StockDTO;
import com.erp_mes.mes.stock.dto.WarehouseDTO;
import com.erp_mes.mes.stock.mapper.StockMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class StockService {
    
    private final StockMapper stockMapper;
    
    // 재고 목록 조회
    @Transactional(readOnly = true)
    public List<StockDTO> getStockList(String productName, String warehouseId) {
        log.info("재고 목록 조회 - 품목명: {}, 창고ID: {}", productName, warehouseId);
        return stockMapper.getStockList(productName, warehouseId);
    }
    
    // 창고 목록 조회
    @Transactional(readOnly = true)
    public List<WarehouseDTO> getWarehouseList() {
        log.info("창고 목록 조회");
        return stockMapper.getWarehouseList();
    }
    
    // 재고 상세 조회
    @Transactional(readOnly = true)
    public StockDTO getStockDetail(String productId) {
        log.info("재고 상세 조회 - 품목ID: {}", productId);
        return stockMapper.getStockDetail(productId);
    }
    
    // 재고 수량 업데이트
    @Transactional
    public boolean updateStockAmount(String productId, String warehouseId, Integer itemAmount) {
        log.info("재고 수량 업데이트 - 품목ID: {}, 창고ID: {}, 수량: {}", 
                productId, warehouseId, itemAmount);
        int result = stockMapper.updateStockAmount(productId, warehouseId, itemAmount);
        return result > 0;
    }
    
 // 자재 목록 조회
    @Transactional(readOnly = true)
    public List<ProductDTO> getMaterialList(String productType, String searchKeyword) {
        log.info("자재 목록 조회 서비스");
        return stockMapper.selectMaterialList(productType, searchKeyword);
    }

    // 자재 등록
    @Transactional
    public void addMaterial(ProductDTO dto) {
        log.info("자재 등록: {}", dto.getProductId());
        // 중복 체크
        if(stockMapper.existsMaterialById(dto.getProductId())) {
            throw new RuntimeException("이미 존재하는 자재코드입니다.");
        }
        stockMapper.insertMaterial(dto);
    }

    // 자재 수정(바꾼 사람id로 변경)
    @Transactional
    public boolean updateMaterial(ProductDTO dto, String modifierId) {
        log.info("자재 수정: {} by {}", dto.getProductId(), modifierId);
        dto.setEmpId(modifierId);  
        return stockMapper.updateMaterial(dto) > 0;
    }
    
    // 자재 삭제
    @Transactional
    public Map<String, Object> deleteMaterials(List<String> productIds) {
        log.info("자재 삭제 요청: {} 건", productIds.size());
        
        List<String> canDelete = new ArrayList<>();
        List<String> cannotDelete = new ArrayList<>();
        
        for(String productId : productIds) {
            // 최근 1개월 내 입출고 체크
            int recentCount = stockMapper.checkRecentTransaction(productId);
            
            if(recentCount > 0) {
                cannotDelete.add(productId);
            } else {
                canDelete.add(productId);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        
        // 삭제 가능한 것만 삭제
        if(!canDelete.isEmpty()) {
            stockMapper.deleteMaterials(canDelete);
            result.put("deleted", canDelete.size());
        }
        
        // 삭제 불가능한 항목 알림
        if(!cannotDelete.isEmpty()) {
            result.put("failed", cannotDelete);
            result.put("failedCount", cannotDelete.size());
        }
        
        result.put("success", cannotDelete.isEmpty());
        return result;
    }
    
    @Transactional(readOnly = true)
    public String getEmployeeName(String empId) {
        log.info("직원 이름 조회: {}", empId);
        return stockMapper.selectEmployeeName(empId);
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductList(String productType, String searchKeyword) {
        log.info("제품 목록 조회 - 유형: {}, 검색어: {}", productType, searchKeyword);
        return stockMapper.selectProductList(productType, searchKeyword);
    }
    
    @Transactional
    public void addProduct(ProductDTO dto) {
        log.info("제품 등록: {}", dto.getProductId());
        if(stockMapper.existsMaterialById(dto.getProductId())) {
            throw new RuntimeException("이미 존재하는 제품코드입니다.");
        }
        stockMapper.insertProduct(dto);
    }
    
    @Transactional
    public boolean updateProduct(ProductDTO dto) {
        log.info("제품 수정: {}", dto.getProductId());
        return stockMapper.updateProduct(dto) > 0;
    }
    
    @Transactional
    public Map<String, Object> deleteProducts(List<String> productIds) {
        log.info("제품 삭제 요청: {} 건", productIds.size());
        
        List<String> canDelete = new ArrayList<>();
        List<String> cannotDelete = new ArrayList<>();
        
        for(String productId : productIds) {
            int recentCount = stockMapper.checkRecentTransaction(productId);
            
            if(recentCount > 0) {
                cannotDelete.add(productId);
            } else {
                canDelete.add(productId);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        
        if(!canDelete.isEmpty()) {
            stockMapper.deleteProducts(canDelete);
            result.put("deleted", canDelete.size());
        }
        
        if(!cannotDelete.isEmpty()) {
            result.put("failed", cannotDelete);
        }
        
        result.put("success", cannotDelete.isEmpty());
        return result;
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, String>> getEmployeeList() {
        return stockMapper.selectEmployeeList();
    }
}
