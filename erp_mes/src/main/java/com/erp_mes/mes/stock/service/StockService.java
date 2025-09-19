package com.erp_mes.mes.stock.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.stock.dto.ProductDTO;
import com.erp_mes.erp.config.util.SessionUtil;
import com.erp_mes.erp.config.util.TableMetadataManager;
import com.erp_mes.mes.lot.trace.TrackLot;
import com.erp_mes.mes.stock.dto.MaterialDTO;
import com.erp_mes.mes.stock.dto.StockDTO;
import com.erp_mes.mes.stock.dto.WarehouseDTO;
import com.erp_mes.mes.stock.mapper.StockMapper;

import jakarta.security.auth.message.callback.PrivateKeyCallback.Request;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class StockService {
    
    private final StockMapper stockMapper;
    
    // ==================== 재고 현황 관련 ====================
    
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
    
    // ==================== Material 테이블 관련 (부품/반제품) ====================
    
    // 자재 목록 조회
    @Transactional(readOnly = true)
    public List<MaterialDTO> getMaterialList(String materialType, String searchKeyword) {
        log.info("자재 목록 조회 - material 테이블");
        return stockMapper.selectMaterialListFromMaterial(materialType, searchKeyword);
    }
    
    // 자재 등록
    @Transactional
    @TrackLot // 로트 관련 어노테이션 
    public void addMaterial(MaterialDTO dto) {
        log.info("자재 등록: {}", dto.getMaterialId());
        
        if(stockMapper.existsMaterialById(dto.getMaterialId())) {
            throw new RuntimeException("이미 존재하는 자재코드입니다.");
        }
        
        // 1. material 테이블에 등록
        stockMapper.insertIntoMaterial(dto);
        
//		로트 생성: jpa -> entity(mybatis -> dto) 를 넘겨주는 곳
        HttpSession session = SessionUtil.getSession();
        session.setAttribute("lotDto", dto);
        
        // 2. 자재 타입에 맞는 창고 찾기
        String warehouseType = dto.getMaterialType().equals("부품") ? "부품창고" : "반제품창고";
        List<String> warehouseIds = stockMapper.getActiveWarehousesByType(warehouseType);
        
        if(!warehouseIds.isEmpty()) {
            String warehouseId = warehouseIds.get(0);
            
            // 3. 초기 재고 설정 (필요시)
            // 여기서는 입고를 통해 재고가 추가되므로 초기재고 설정 안함
            log.info("자재 등록 완료 - 창고: {}", warehouseId);
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
        
        // 1. product 테이블에 등록
        stockMapper.insertProduct(dto);
        
        // 2. 완제품 창고 찾기
        List<String> warehouseIds = stockMapper.getActiveWarehousesByType("완제품창고");
        
        if(!warehouseIds.isEmpty()) {
            String warehouseId = warehouseIds.get(0);
            log.info("제품 등록 완료 - 창고: {}", warehouseId);
        }
    }
    
    // 제품 수정
    @Transactional
    public boolean updateProduct(ProductDTO dto) {
        log.info("제품 수정: {}", dto.getProductId());
        return stockMapper.updateProduct(dto) > 0;
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
                throw new RuntimeException("창고 공간이 부족합니다.");
            }
            
            String newLocation = emptyLocations.get(0);
            int storeQty = Math.min(remainingQty, 500);
            
            stockMapper.insertWarehouseItemWithLocation(
                productId, warehouseId, newLocation, storeQty, empId
            );
            
            remainingQty -= storeQty;
        }
    }
    
    // 재고 차감 (출고)
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
    
    // ==================== 공통 ====================
    
    @Transactional(readOnly = true)
    public String getEmployeeName(String empId) {
        return stockMapper.selectEmployeeName(empId);
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, String>> getEmployeeList() {
        return stockMapper.selectEmployeeList();
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, String>> getMaterialTypes() {
        log.info("공통코드에서 자재타입 조회");
        return stockMapper.getMaterialTypes();
    }
}