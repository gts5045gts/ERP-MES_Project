package com.erp_mes.mes.stock.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.stock.dto.ProductDTO;
import com.erp_mes.erp.config.util.SessionUtil;
import com.erp_mes.mes.lot.trace.TrackLot;
import com.erp_mes.mes.stock.dto.MaterialDTO;
import com.erp_mes.mes.stock.dto.StockDTO;
import com.erp_mes.mes.stock.dto.WarehouseDTO;
import com.erp_mes.mes.stock.mapper.StockMapper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class StockService {
    
    private final StockMapper stockMapper;
    
    // ==================== 재고 현황 관련 ====================
    
    // 전체 재고 목록 조회 (material + product 통합)
    @Transactional(readOnly = true)
    public List<StockDTO> getAllStockList(String productName, String warehouseId) {
        log.info("전체 재고 목록 조회 - 품목명: {}", productName);
        return stockMapper.getAllStockList(productName, warehouseId);
    }
    
    // 재고 목록 조회 (product만)
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
    
    // 재고 상세 정보 조회
    @Transactional(readOnly = true)
    public StockDTO getStockDetail(String productId) {
        log.info("재고 상세 조회 - 품목ID: {}", productId);
        return stockMapper.getStockDetail(productId);
    }
    
    // 재고 수량 직접 업데이트
    @Transactional
    public boolean updateStockAmount(String productId, String warehouseId, Integer itemAmount) {
        log.info("재고 수량 업데이트 - 품목ID: {}, 창고ID: {}, 수량: {}", 
                productId, warehouseId, itemAmount);
        int result = stockMapper.updateStockAmount(productId, warehouseId, itemAmount);
        return result > 0;
    }
    
    // ==================== Material 재고 차감 관련 ====================
    
    // Material 재고 차감 (생산 투입용)
    @Transactional
    public boolean reduceMaterialStock(String materialId, Integer reduceQty) {
        log.info("Material 재고 차감 - materialId: {}, 차감수량: {}", materialId, reduceQty);
        
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
                reduceMaterialWarehouseStock(materialId, warehouseId, reduceQty);
                log.info("Material {} 재고 {} 차감 완료", materialId, reduceQty);
                return true;
            }
        }
        
        return false;
    }
    
    // 특정 창고에서 Material 재고 차감
    @Transactional
    public boolean reduceMaterialStockFromWarehouse(String materialId, String warehouseId, 
            String locationId, Integer reduceQty, String reason, String empId) {
        
        log.info("특정 창고에서 Material 재고 차감 - materialId: {}, warehouseId: {}, locationId: {}, 차감수량: {}", 
                 materialId, warehouseId, locationId, reduceQty);
        
        // warehouse_item 재고 확인 및 차감
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
        
        // material 테이블 수량 동기화
        stockMapper.reduceMaterialStock(materialId, reduceQty);
        
        log.info("Material {} 재고 {} 차감 완료 (창고: {}, 위치: {})", 
                 materialId, reduceQty, warehouseId, locationId);
        
        return true;
    }
    
    // Material warehouse_item 재고 차감 (내부 메서드)
    private void reduceMaterialWarehouseStock(String materialId, String warehouseId, Integer qty) {
        List<Map<String, Object>> locations = stockMapper.getMaterialLocationsByQty(materialId, warehouseId);
        
        if(locations.isEmpty()) {
            log.warn("warehouse_item에 해당 자재가 없습니다: {}", materialId);
            return;
        }
        
        int remainingQty = qty;
        
        // 수량 적은 곳부터 차감
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
    
    // Material 창고별 재고 현황 조회
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
    
    // 자재 신규 등록
    @Transactional
    @TrackLot(tableName = "material", pkColumnName = "material_id") // ******로트 관련 어노테이션 //테이블명, pk_id 입력****** 
    public void addMaterial(MaterialDTO dto) {
        log.info("자재 등록: {}", dto.getMaterialId());
        
        // 중복 체크
        if(stockMapper.existsMaterialById(dto.getMaterialId())) {
            throw new RuntimeException("이미 존재하는 자재코드입니다.");
        }
        
        // material 테이블 등록 (기본 수량 100)
        if(dto.getQuantity() == null || dto.getQuantity() == 0) {
            dto.setQuantity(100);
        }
        stockMapper.insertIntoMaterial(dto);
        
//		*******로트 생성: pk value 를 넘겨주는 곳**********
        HttpSession session = SessionUtil.getSession();
        session.setAttribute("targetIdValue", dto.getMaterialId()); //pk_id의 값 입력
        
        // 자재 타입별 창고 배정 및 warehouse_item 등록
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
                
                distributeStock(dto.getMaterialId(), warehouseId, dto.getQuantity(), dto.getEmpId());
                
                log.info("자재 등록 완료 - 창고: {}, 수량: {}", warehouseId, dto.getQuantity());
            } else {
                log.warn("해당 타입의 창고가 없습니다: {}", warehouseType);
            }
        }
    }
    
    // 자재 정보 수정
    @Transactional
    public boolean updateMaterial(MaterialDTO dto, String modifierId) {
        log.info("자재 수정: {} by {}", dto.getMaterialId(), modifierId);
        dto.setEmpId(modifierId);
        return stockMapper.updateMaterialTable(dto) > 0;
    }
    
    // 자재 삭제 (다중 선택)
    @Transactional
    public Map<String, Object> deleteMaterials(List<String> materialIds) {
        log.info("자재 삭제 요청: {} 건", materialIds.size());
        
        List<String> canDelete = new ArrayList<>();
        List<String> cannotDelete = new ArrayList<>();
        
        // 최근 거래 확인
        for(String materialId : materialIds) {
            int recentCount = stockMapper.checkRecentTransactionForMaterial(materialId);
            
            if(recentCount > 0) {
                cannotDelete.add(materialId);
            } else {
                canDelete.add(materialId);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        
        // 삭제 가능한 것만 처리
        if(!canDelete.isEmpty()) {
            for(String materialId : canDelete) {
                stockMapper.deleteWarehouseItemsByMaterial(materialId);
            }
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
    
    // 제품 신규 등록
    @Transactional
    public void addProduct(ProductDTO dto) {
        log.info("제품 등록: {}", dto.getProductId());
        
        // 중복 체크
        if(stockMapper.existsMaterialById(dto.getProductId())) {
            throw new RuntimeException("이미 존재하는 제품코드입니다.");
        }
        
        // product 테이블 등록
        if(dto.getQuantity() == null) {
            dto.setQuantity(0);
        }
        stockMapper.insertProduct(dto);
        
        // 완제품인 경우 창고 배정
        if("PTYPE001".equals(dto.getProductType())) {
            List<String> warehouseIds = stockMapper.getActiveWarehousesByType("완제품");
            
            if(!warehouseIds.isEmpty() && dto.getQuantity() > 0) {
                String warehouseId = warehouseIds.get(0);
                distributeStock(dto.getProductId(), warehouseId, dto.getQuantity(), dto.getEmpId());
                log.info("제품 등록 완료 - 창고: {}, 수량: {}", warehouseId, dto.getQuantity());
            }
        }
    }
    
    // 제품 정보 수정
    @Transactional
    public boolean updateProduct(ProductDTO dto) {
        log.info("제품 수정: {}", dto.getProductId());
        return stockMapper.updateProduct(dto) > 0;
    }
    
    // 제품 삭제 (다중 선택)
    @Transactional
    public Map<String, Object> deleteProducts(List<String> productIds) {
        log.info("제품 삭제 요청: {} 건", productIds.size());
        
        List<String> canDelete = new ArrayList<>();
        List<String> cannotDelete = new ArrayList<>();
        
        // 최근 거래 확인
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
    
    // 제품별 창고 재고 현황 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getWarehouseStockByProduct(String productId) {
        return stockMapper.getWarehouseStockByProduct(productId);
    }
    
    // 창고별 재고 조정 (입고/출고)
    @Transactional
    public boolean adjustWarehouseStock(String productId, String warehouseId, 
            Integer adjustQty, String adjustType, String reason, String empId) {
        
        log.info("재고조정 시작 - productId: {}, warehouseId: {}, adjustQty: {}, type: {}", 
                 productId, warehouseId, adjustQty, adjustType);
        
        try {
            if("IN".equals(adjustType)) {
                distributeStock(productId, warehouseId, adjustQty, empId);
            } else if("OUT".equals(adjustType)) {
                reduceStock(productId, warehouseId, adjustQty);
            }
            
            // 전체 재고 동기화
            Integer totalStock = stockMapper.getTotalStockByProduct(productId);
            stockMapper.updateProductQuantity(productId, totalStock);
            
            return true;
            
        } catch(Exception e) {
            log.error("재고조정 실패: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }
    
    // 재고 차감 처리 (출고용)
    private void reduceStock(String productId, String warehouseId, Integer qty) {
        List<Map<String, Object>> locations = 
            stockMapper.getProductLocationsByQty(productId, warehouseId);
        
        if(locations.isEmpty()) {
            throw new RuntimeException("해당 제품의 재고가 없습니다.");
        }
        
        int remainingQty = qty;
        
        // 수량 적은 곳부터 차감
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
    
    // 재고 분산 저장 (입고용)
    private void distributeStock(String productId, String warehouseId, Integer qty, String empId) {
        if(qty == null || qty <= 0) {
            return;
        }
        
        // Material인지 Product인지 확인
        boolean isMaterial = stockMapper.existsMaterialById(productId);
        
        // 해당 타입의 모든 창고 가져오기
        String warehouseType = "";
        if(isMaterial) {
            MaterialDTO material = stockMapper.selectMaterialById(productId);
            warehouseType = material.getMaterialType().equals("부품") ? "원자재" : "반제품";
        } else {
            warehouseType = "완제품";
        }
        
        List<String> allWarehouses = stockMapper.getActiveWarehousesByType(warehouseType);
        int remainingQty = qty;
        
        // 모든 창고 순회하면서 분산 저장
        for(String currentWarehouseId : allWarehouses) {
            if(remainingQty <= 0) break;
            
            // 기존 위치 채우기
            List<Map<String, Object>> existingLocations = 
                stockMapper.getProductLocationsWithSpace(productId, currentWarehouseId);
            
            for(Map<String, Object> loc : existingLocations) {
                if(remainingQty <= 0) break;
                
                String locationId = (String) loc.get("locationId");
                int currentQty = ((Number) loc.get("itemAmount")).intValue();
                int space = 1000 - currentQty;
                
                if(space > 0) {
                    int addQty = Math.min(remainingQty, space);
                    stockMapper.updateLocationStock(productId, currentWarehouseId, locationId, currentQty + addQty);
                    remainingQty -= addQty;
                    log.info("창고 {} 위치 {}에 {} 개 추가", currentWarehouseId, locationId, addQty);
                }
            }
            
            // 빈 위치 찾아서 저장
            while(remainingQty > 0) {
                List<String> emptyLocations = stockMapper.getEmptyLocations(currentWarehouseId);
                if(emptyLocations.isEmpty()) {
                    log.info("창고 {}가 가득 참. 다음 창고로 이동", currentWarehouseId);
                    break;  // 다음 창고로
                }
                
                String newLocation = emptyLocations.get(0);
                int storeQty = Math.min(remainingQty, 1000);
                
                if(isMaterial) {
                    stockMapper.insertMaterialStock(
                        productId, currentWarehouseId, newLocation, storeQty, empId
                    );
                } else {
                    stockMapper.insertWarehouseItemWithLocation(
                        productId, currentWarehouseId, newLocation, storeQty, empId
                    );
                }
                
                remainingQty -= storeQty;
                log.info("창고 {} 위치 {}에 {} 개 저장", currentWarehouseId, newLocation, storeQty);
            }
        }
        
        if(remainingQty > 0) {
            log.error("모든 {} 창고가 가득 참! 남은 수량: {}", warehouseType, remainingQty);
        }
    }
    
    // ==================== 공통 기능 ====================
    
    // 자재 타입 공통코드 조회
    @Transactional(readOnly = true)
    public List<Map<String, String>> getMaterialTypes() {
        log.info("공통코드에서 자재타입 조회");
        return stockMapper.getMaterialTypes();
    }
    
    // 검사방법 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getInspectionMethods() {
        return stockMapper.getInspectionMethods();
    }
    
    // 단위 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUnitList() {
        return stockMapper.getUnitList();
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

    // 품질기준정보 등록시 사용 - 품질관리
    public List<MaterialDTO> getMaterialList() {
    	
        return stockMapper.findAllMaterials();
    }
}