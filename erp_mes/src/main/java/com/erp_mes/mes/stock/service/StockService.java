package com.erp_mes.mes.stock.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.stock.dto.ProductDTO;
import com.erp_mes.mes.stock.dto.MaterialDTO;
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
    
    // ==================== 재고 현황 관련 ====================
    
    // 전체 재고 목록 조회 (material + product)
    @Transactional(readOnly = true)
    public List<StockDTO> getAllStockList(String productName, String warehouseId) {
        log.info("전체 재고 목록 조회 - 품목명: {}", productName);
        return stockMapper.getAllStockList(productName, warehouseId);
    }
    
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
    
    // Material 재고 차감 (투입용)
    @Transactional
    public boolean reduceMaterialStock(String materialId, Integer reduceQty) {
        log.info("Material 재고 차감 - materialId: {}, 차감수량: {}", materialId, reduceQty);
        
        // 1. material 테이블의 quantity 차감
        int result = stockMapper.reduceMaterialStock(materialId, reduceQty);
        
        if(result > 0) {
            // 2. warehouse_item 테이블에서도 차감 처리
            MaterialDTO material = stockMapper.selectMaterialById(materialId);
            String warehouseType = "";
            
            if(material.getMaterialType().equals("부품")) {
                warehouseType = "원자재";
            } else if(material.getMaterialType().equals("반제품")) {
                warehouseType = "반제품";
            }
            
            if(!warehouseType.isEmpty()) {
                List<String> warehouseIds = stockMapper.getActiveWarehousesByType(warehouseType);
                if(!warehouseIds.isEmpty()) {
                    String warehouseId = warehouseIds.get(0);
                    
                    // warehouse_item에서 차감
                    reduceMaterialWarehouseStock(materialId, warehouseId, reduceQty);
                }
            }
            
            log.info("Material {} 재고 {} 차감 완료", materialId, reduceQty);
            return true;
        }
        
        return false;
    }
    
    // 특정 창고에서 Material 재고 차감
    @Transactional
    public boolean reduceMaterialStockFromWarehouse(String materialId, String warehouseId, 
            String locationId, Integer reduceQty, String reason, String empId) {
        
        log.info("특정 창고에서 Material 재고 차감 - materialId: {}, warehouseId: {}, locationId: {}, 차감수량: {}", 
                 materialId, warehouseId, locationId, reduceQty);
        
        // 1. warehouse_item에서 차감
        int currentQty = stockMapper.getWarehouseItemQtyByLocation(materialId, warehouseId, locationId);
        
        if(currentQty < reduceQty) {
            throw new RuntimeException("재고가 부족합니다.");
        }
        
        int newQty = currentQty - reduceQty;
        if(newQty == 0) {
            stockMapper.deleteEmptyMaterialLocation(materialId, warehouseId, locationId);
        } else {
            stockMapper.updateMaterialLocationStock(materialId, warehouseId, locationId, newQty);
        }
        
        // 2. material 테이블의 quantity도 차감
        stockMapper.reduceMaterialStock(materialId, reduceQty);
        
        log.info("Material {} 재고 {} 차감 완료 (창고: {}, 위치: {})", 
                 materialId, reduceQty, warehouseId, locationId);
        
        return true;
    }
    
    private void reduceMaterialWarehouseStock(String materialId, String warehouseId, Integer qty) {
        List<Map<String, Object>> locations = stockMapper.getMaterialLocationsByQty(materialId, warehouseId);
        
        if(locations.isEmpty()) {
            log.warn("warehouse_item에 해당 자재가 없습니다: {}", materialId);
            return;
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
                    stockMapper.deleteEmptyMaterialLocation(materialId, warehouseId, locationId);
                } else {
                    stockMapper.updateMaterialLocationStock(materialId, warehouseId, locationId, newQty);
                }
                
                remainingQty -= reduceQty;
            }
        }
    }
    
    // Material 창고별 재고 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMaterialWarehouseStock(String materialId) {
        return stockMapper.getMaterialWarehouseStock(materialId);
    }
    
    // ==================== Material 테이블 관련 (부품/반제품) ====================
    
    // 자재 목록 조회
    @Transactional(readOnly = true)
    public List<MaterialDTO> getMaterialList(String materialType, String searchKeyword) {
        log.info("자재 목록 조회 - material 테이블");
        return stockMapper.selectMaterialListFromMaterial(materialType, searchKeyword);
    }
    
    // 자재 등록
    @Transactional
    public void addMaterial(MaterialDTO dto) {
        log.info("자재 등록: {}", dto.getMaterialId());
        
        if(stockMapper.existsMaterialById(dto.getMaterialId())) {
            throw new RuntimeException("이미 존재하는 자재코드입니다.");
        }
        
        // 1. material 테이블에 등록 (quantity 기본값 100 설정)
        if(dto.getQuantity() == null || dto.getQuantity() == 0) {
            dto.setQuantity(100);  // 기본값 100
        }
        stockMapper.insertIntoMaterial(dto);
        
        // 2. 자재 타입에 맞는 창고 찾기 및 warehouse_item 등록
        String warehouseType = "";
        if(dto.getMaterialType().equals("부품")) {
            warehouseType = "원자재";
        } else if(dto.getMaterialType().equals("반제품")) {
            warehouseType = "반제품";
        }
        
        if(!warehouseType.isEmpty()) {
            List<String> warehouseIds = stockMapper.getActiveWarehousesByType(warehouseType);
            
            if(!warehouseIds.isEmpty()) {
                String warehouseId = warehouseIds.get(0);
                
                // 3. warehouse_item에 초기 재고 분산 저장 (기본 100개)
                distributeStock(dto.getMaterialId(), warehouseId, dto.getQuantity(), dto.getEmpId());
                
                log.info("자재 등록 완료 - 창고: {}, 수량: {}", warehouseId, dto.getQuantity());
            }
        }
    }
    
    // 자재 수정
    @Transactional
    public boolean updateMaterial(MaterialDTO dto, String modifierId) {
        log.info("자재 수정: {} by {}", dto.getMaterialId(), modifierId);
        dto.setEmpId(modifierId);
        return stockMapper.updateMaterialTable(dto) > 0;
    }
    
    // 자재 삭제
    @Transactional
    public Map<String, Object> deleteMaterials(List<String> materialIds) {
        log.info("자재 삭제 요청: {} 건", materialIds.size());
        
        List<String> canDelete = new ArrayList<>();
        List<String> cannotDelete = new ArrayList<>();
        
        for(String materialId : materialIds) {
            // 최근 1개월 내 입출고 체크
            int recentCount = stockMapper.checkRecentTransactionForMaterial(materialId);
            
            if(recentCount > 0) {
                cannotDelete.add(materialId);
            } else {
                canDelete.add(materialId);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        
        if(!canDelete.isEmpty()) {
            // warehouse_item에서 먼저 삭제
            for(String materialId : canDelete) {
                stockMapper.deleteWarehouseItemsByMaterial(materialId);
            }
            // material 테이블에서 삭제
            stockMapper.deleteMaterialsFromTable(canDelete);
            result.put("deleted", canDelete.size());
        }
        
        if(!cannotDelete.isEmpty()) {
            result.put("failed", cannotDelete);
            result.put("failedCount", cannotDelete.size());
        }
        
        result.put("success", cannotDelete.isEmpty());
        return result;
    }
    
    // ==================== Product 테이블 관련 (완제품) ====================
    
    // 제품 목록 조회
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
        
        // 1. product 테이블에 등록 (quantity 초기값 설정)
        if(dto.getQuantity() == null) {
            dto.setQuantity(0);
        }
        stockMapper.insertProduct(dto);
        
        // 2. PTYPE001인 경우 완제품 창고 찾기
        if("PTYPE001".equals(dto.getProductType())) {
            List<String> warehouseIds = stockMapper.getActiveWarehousesByType("완제품");
            
            if(!warehouseIds.isEmpty() && dto.getQuantity() > 0) {
                String warehouseId = warehouseIds.get(0);
                
                // 3. warehouse_item에 초기 재고 분산 저장
                distributeStock(dto.getProductId(), warehouseId, dto.getQuantity(), dto.getEmpId());
                
                log.info("제품 등록 완료 - 창고: {}, 수량: {}", warehouseId, dto.getQuantity());
            }
        }
    }
    
    // 검사방법 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getInspectionMethods() {
        return stockMapper.getInspectionMethods();
    }
    
    // 제품 수정
    @Transactional
    public boolean updateProduct(ProductDTO dto) {
        log.info("제품 수정: {}", dto.getProductId());
        return stockMapper.updateProduct(dto) > 0;
    }
    
    // 단위 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUnitList() {
        return stockMapper.getUnitList();
    }
    
    // 제품 삭제
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
    
    // ==================== 재고 조정 관련 ====================
    
    // 특정 제품의 창고별 재고 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getWarehouseStockByProduct(String productId) {
        return stockMapper.getWarehouseStockByProduct(productId);
    }
    
    // 재고 차감 메서드 (출고)
    private void reduceStock(String productId, String warehouseId, Integer qty) {
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
                    stockMapper.deleteEmptyLocation(productId, warehouseId, locationId);
                } else {
                    stockMapper.updateLocationStock(productId, warehouseId, locationId, newQty);
                }
                
                remainingQty -= reduceQty;
            }
        }
        
        if(remainingQty > 0) {
            throw new RuntimeException("재고가 부족합니다.");
        }
    }
    
    // 창고별 재고 조정
    @Transactional
    public boolean adjustWarehouseStock(String productId, String warehouseId, 
            Integer adjustQty, String adjustType, String reason, String empId) {
        
        log.info("재고조정 시작 - productId: {}, warehouseId: {}, adjustQty: {}, type: {}", 
                 productId, warehouseId, adjustQty, adjustType);
        
        try {
            if("IN".equals(adjustType)) {
                // 입고 처리
                distributeStock(productId, warehouseId, adjustQty, empId);
            } else if("OUT".equals(adjustType)) {
                // 출고 처리
                reduceStock(productId, warehouseId, adjustQty);
            }
            
            // product 테이블의 전체 재고 업데이트
            Integer totalStock = stockMapper.getTotalStockByProduct(productId);
            stockMapper.updateProductQuantity(productId, totalStock);
            
            return true;
            
        } catch(Exception e) {
            log.error("재고조정 실패: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }
    
    // 재고 분산 (입고)
    private void distributeStock(String productId, String warehouseId, Integer qty, String empId) {
        if(qty == null || qty <= 0) {
            return;
        }
        
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
            }
        }
        
        // 새 위치에 배정
        while(remainingQty > 0) {
            List<String> emptyLocations = stockMapper.getEmptyLocations(warehouseId);
            if(emptyLocations.isEmpty()) {
                log.warn("창고 공간이 부족합니다. 남은 수량: {}", remainingQty);
                break;
            }
            
            String newLocation = emptyLocations.get(0);
            int storeQty = Math.min(remainingQty, 500);
            
            // Material인지 Product인지 확인해서 다른 메서드 호출
            boolean isMaterial = stockMapper.existsMaterialById(productId);
            
            if(isMaterial) {
                stockMapper.insertMaterialStock(
                    productId, warehouseId, newLocation, storeQty, empId
                );
            } else {
                stockMapper.insertWarehouseItemWithLocation(
                    productId, warehouseId, newLocation, storeQty, empId
                );
            }
            remainingQty -= storeQty;
        }
    }
    
    // 자재 타입 조회
    @Transactional(readOnly = true)
    public List<Map<String, String>> getMaterialTypes() {
        log.info("공통코드에서 자재타입 조회");
        return stockMapper.getMaterialTypes();
    }
    
    // 직원 이름 조회
    @Transactional(readOnly = true)
    public String getEmployeeName(String empId) {
        return stockMapper.selectEmployeeName(empId);
    }

    // 직원 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, String>> getEmployeeList() {
        return stockMapper.selectEmployeeList();
    }
}