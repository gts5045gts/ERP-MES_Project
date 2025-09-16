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
        
        if(stockMapper.existsMaterialById(dto.getProductId())) {
            throw new RuntimeException("이미 존재하는 자재코드입니다.");
        }
        
        // 1. product 테이블에 등록
        stockMapper.insertMaterial(dto);
        
        // 2. 원자재 창고 중 운영중인 창고 조회
        List<String> warehouseIds = stockMapper.getActiveWarehousesByType("원자재");
        if(!warehouseIds.isEmpty()) {
            String warehouseId = warehouseIds.get(0); // 첫번째 창고 선택
            
            // 3. 해당 창고의 빈 위치 조회 및 재고 분산
            List<String> emptyLocations = stockMapper.getEmptyLocations(warehouseId);
            
            int totalQty = 1000;
            int qtyPerLocation = 500; // 칸당 500개
            int locationsNeeded = (int) Math.ceil((double) totalQty / qtyPerLocation);
            
            for(int i = 0; i < Math.min(locationsNeeded, emptyLocations.size()); i++) {
                int qty = (i == locationsNeeded - 1) ? 
                          totalQty - (qtyPerLocation * i) : qtyPerLocation;
                
                stockMapper.insertWarehouseItemWithLocation(
                    dto.getProductId(), 
                    warehouseId, 
                    emptyLocations.get(i), 
                    qty, 
                    dto.getEmpId()
                );
            }
        }
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
        log.info("삭제 가능: {}, 삭제 불가: {}", canDelete, cannotDelete);
        Map<String, Object> result = new HashMap<>();
        
        // 삭제 가능한 것만 삭제
        if(!canDelete.isEmpty()) {
            // 1. 먼저 warehouse_item에서 삭제!
            for(String productId : canDelete) {
                stockMapper.deleteWarehouseItemsByProduct(productId);
            }
            // 2. 그 다음 product에서 삭제
            stockMapper.deleteMaterials(canDelete);
            result.put("deleted", canDelete.size());
        }
        
        // 삭제 불가능한 항목 알림
        if(!cannotDelete.isEmpty()) {
            result.put("failed", cannotDelete);
            result.put("failedCount", cannotDelete.size());
            result.put("message", "최근 입출고 이력이 있어 삭제할 수 없습니다.");
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
    
    // 제품 등록
    @Transactional
    public void addProduct(ProductDTO dto) {
        log.info("제품 등록: {}", dto.getProductId());
        
        if(stockMapper.existsMaterialById(dto.getProductId())) {
            throw new RuntimeException("이미 존재하는 제품코드입니다.");
        }
        
        stockMapper.insertProduct(dto);
        
        String warehouseType = dto.getProductType().equals("완제품") ? "완제품" : "반제품";
        List<String> warehouseIds = stockMapper.getActiveWarehousesByType(warehouseType);
        
        if(!warehouseIds.isEmpty()) {
            String selectedWarehouseId = warehouseIds.get(0);
            stockMapper.insertWarehouseItem(dto.getProductId(), selectedWarehouseId, 1000, dto.getEmpId());
        }
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
    
    // 0916
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getWarehouseStockByProduct(String productId) {
        return stockMapper.getWarehouseStockByProduct(productId);
    }
    // 0916
    @Transactional
    public boolean adjustWarehouseStock(String productId, String warehouseId, 
            Integer adjustQty, String adjustType, String reason, String empId) {
        
        log.info("재고조정 시작 - productId: {}, warehouseId: {}, adjustQty: {}, type: {}", 
                 productId, warehouseId, adjustQty, adjustType);
        
        try {
            if("IN".equals(adjustType)) {
                // 입고 처리 - 500개씩 분산
                distributeStock(productId, warehouseId, adjustQty, empId);
            } else if("OUT".equals(adjustType)) {
                // 출고 처리 - 적은 수량부터 차감
                reduceStock(productId, warehouseId, adjustQty);
            }
            
            // product 테이블의 전체 재고 업데이트
            Integer totalStock = stockMapper.getTotalStockByProduct(productId);
            log.info("전체 재고 계산: {}", totalStock);
            
            int updateCount = stockMapper.updateProductQuantity(productId, totalStock);
            log.info("product 테이블 업데이트 결과: {} 행", updateCount);
            
            return true;
            
        } catch(Exception e) {
            log.error("재고조정 실패: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private void distributeStock(String productId, String warehouseId, Integer qty, String empId) {
        // 현재 제품이 있는 위치들 조회 (500개 미만인 곳)
        List<Map<String, Object>> existingLocations = 
            stockMapper.getProductLocationsWithSpace(productId, warehouseId);
        
        int remainingQty = qty;
        
        // 기존 위치에 먼저 채우기
        for(Map<String, Object> loc : existingLocations) {
            if(remainingQty <= 0) break;
            
            String locationId = (String) loc.get("locationId");
            int currentQty = ((Number) loc.get("itemAmount")).intValue();
            int space = 500 - currentQty;
            
            if(space > 0) {
                int addQty = Math.min(remainingQty, space);
                stockMapper.updateLocationStock(productId, warehouseId, locationId, currentQty + addQty);
                remainingQty -= addQty;
                log.info("기존 위치 {} 에 {} 개 추가", locationId, addQty);
            }
        }
        
        // 남은 수량이 있으면 새 위치에 배정
        while(remainingQty > 0) {
            List<String> emptyLocations = stockMapper.getEmptyLocations(warehouseId);
            if(emptyLocations.isEmpty()) {
                throw new RuntimeException("창고 공간이 부족합니다. 남은 수량: " + remainingQty);
            }
            
            String newLocation = emptyLocations.get(0);
            int storeQty = Math.min(remainingQty, 500);
            
            stockMapper.insertWarehouseItemWithLocation(
                productId, warehouseId, newLocation, storeQty, empId
            );
            
            log.info("새 위치 {} 에 {} 개 배정", newLocation, storeQty);
            remainingQty -= storeQty;
        }
    }

    // 출고 시 적은 수량부터 차감
    private void reduceStock(String productId, String warehouseId, Integer qty) {
        // 적은 수량 순으로 위치 조회
        List<Map<String, Object>> locations = 
            stockMapper.getProductLocationsByQty(productId, warehouseId);
        
        if(locations.isEmpty()) {
            throw new RuntimeException("해당 제품의 재고가 없습니다.");
        }
        
        int remainingQty = qty;
        
        for(Map<String, Object> loc : locations) {
            if(remainingQty <= 0) break;
            
            String locationId = (String) loc.get("locationId");
            int currentQty = ((Number) loc.get("itemAmount")).intValue();
            
            if(currentQty > 0) {
                int reduceQty = Math.min(remainingQty, currentQty);
                int newQty = currentQty - reduceQty;
                
                if(newQty == 0) {
                    // 0개가 되면 삭제
                    stockMapper.deleteEmptyLocation(productId, warehouseId, locationId);
                    log.info("위치 {} 재고 소진 - 삭제", locationId);
                } else {
                    // 수량만 업데이트
                    stockMapper.updateLocationStock(productId, warehouseId, locationId, newQty);
                    log.info("위치 {} 에서 {} 개 차감", locationId, reduceQty);
                }
                
                remainingQty -= reduceQty;
            }
        }
        
        if(remainingQty > 0) {
            throw new RuntimeException("재고가 부족합니다. 부족 수량: " + remainingQty);
        }
    }
}