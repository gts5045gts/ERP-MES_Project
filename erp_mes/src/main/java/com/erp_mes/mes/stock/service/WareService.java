package com.erp_mes.mes.stock.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.erp.config.util.SessionUtil;
import com.erp_mes.mes.lot.trace.TrackLot;
import com.erp_mes.mes.stock.dto.WarehouseDTO;
import com.erp_mes.mes.stock.mapper.WareMapper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class WareService {
    
    private final WareMapper wareMapper;
    
    // ==================== 창고 관리 ====================
    
    // 창고 목록 조회
    @Transactional(readOnly = true)
    public List<WarehouseDTO> getWarehouseList(String warehouseType, String warehouseStatus, String searchKeyword) {
        log.info("창고 목록 조회 - 유형: {}, 상태: {}, 검색어: {}", warehouseType, warehouseStatus, searchKeyword);
        return wareMapper.selectWarehouseList(warehouseType, warehouseStatus, searchKeyword);
    }
    
    // 창고 타입별 목록 조회
    @Transactional(readOnly = true)
    public List<WarehouseDTO> getWarehouseListByType(String warehouseType) {
        return wareMapper.selectWarehouseListByType(warehouseType);
    }
    
    // 신규 창고 등록
    @Transactional
    public void addWarehouse(WarehouseDTO dto) {
        log.info("창고 등록: {}", dto.getWarehouseId());
        
        if(wareMapper.existsWarehouseById(dto.getWarehouseId())) {
            throw new RuntimeException("이미 존재하는 창고ID입니다.");
        }
        
        wareMapper.insertWarehouse(dto);
    }
    
    // 창고 정보 수정
    @Transactional
    public boolean updateWarehouse(WarehouseDTO dto) {
        log.info("창고 수정: {}", dto.getWarehouseId());
        return wareMapper.updateWarehouse(dto) > 0;
    }
    
    // 창고 삭제 (재고 확인)
    @Transactional
    public Map<String, Object> deleteWarehouses(List<String> warehouseIds) {
        log.info("창고 삭제 요청: {} 건", warehouseIds.size());
        
        List<String> canDelete = new ArrayList<>();
        List<String> cannotDelete = new ArrayList<>();
        
        // 재고 보유 여부 확인
        for(String warehouseId : warehouseIds) {
            int inUseCount = wareMapper.checkWarehouseInUse(warehouseId);
            
            if(inUseCount > 0) {
                cannotDelete.add(warehouseId);
            } else {
                canDelete.add(warehouseId);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        
        // 삭제 가능한 것만 처리
        if(!canDelete.isEmpty()) {
            wareMapper.deleteWarehouses(canDelete);
            result.put("deleted", canDelete.size());
        }
        
        if(!cannotDelete.isEmpty()) {
            result.put("failed", cannotDelete);
            result.put("failedCount", cannotDelete.size());
        }
        
        result.put("success", cannotDelete.isEmpty());
        return result;
    }
    
    // ==================== 입고 관리 ====================
    
    // 입고 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getInputList(String inType, String inStatus) {
        return wareMapper.selectInputList(inType, inStatus);
    }
    
    // 배치별 입고 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getInputListByBatch(String batchId) {
        return wareMapper.selectInputListByBatch(batchId);
    }
    
    // 날짜별 그룹화된 입고 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getGroupedInputList(String date, String inType) {
        return wareMapper.selectGroupedInputList(date, inType);
    }

    // 개별 입고 등록
    @Transactional
    @TrackLot(tableName = "INPUT", pkColumnName = "IN_ID")
    public String addInput(Map<String, Object> params) {
        String itemType = (String) params.get("itemType");
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        Integer todayCount = wareMapper.getTodayInputCount(today);
        if(todayCount == null) todayCount = 0;
        
        String inId = "IN" + today + String.format("%03d", todayCount + 1);
        
        params.put("inId", inId);
        
        // 완제품/부품 구분 처리
        if("product".equals(itemType)) {
            // 완제품은 바로 입고완료 상태로
            params.put("inStatus", "입고완료");
            params.put("productId", params.get("productId"));
            
            // 입고 등록
            wareMapper.insertInput(params);
            
            // 바로 재고 처리
            String productId = (String) params.get("productId");
            String warehouseId = (String) params.get("warehouseId");
            Integer inCount = Integer.parseInt(params.get("inCount").toString());
            String empId = (String) params.get("empId");
            
            // product 테이블 수량 증가
            wareMapper.updateProductQuantity(productId, inCount);
            
            // warehouse_item 분산 저장
            String firstLocation = distributeToWarehouseItemsForProduct(warehouseId, productId, inCount, empId);
            
            if(firstLocation != null) {
                wareMapper.updateInputLocation(inId, firstLocation);
            }
            
            log.info("완제품 입고 즉시 완료: {} ({}개)", inId, inCount);
            
        } else {
            // 부품/반제품은 기존대로 입고대기
            params.put("inStatus", "입고대기");
            String materialId = (String) params.get("productId");
            params.put("materialId", materialId);
            params.remove("productId");
            
            wareMapper.insertInput(params);
            log.info("부품/반제품 입고 대기: {}", inId);
        }
        HttpSession session = SessionUtil.getSession();
        session.setAttribute("targetIdValue", inId);
        return inId;
    }
    
    // 오늘 배치 건수 조회
    public Integer getTodayBatchCount(String today) {
        Integer count = wareMapper.getTodayBatchCount(today);
        return count != null ? count : 0;
    }

    // 입고 검사 완료 처리 (로트 처리)
    @Transactional
    @TrackLot(tableName = "input", pkColumnName = "IN_ID") 
    public void completeInput(String inId, String empId) {
        Map<String, Object> input = wareMapper.selectInputById(inId);
        
        if(input == null) {
            throw new RuntimeException("입고 정보를 찾을 수 없습니다.");
        }
        
        String currentStatus = (String) input.get("IN_STATUS");
        
        if("입고완료".equals(currentStatus)) {
            log.info("이미 입고완료 처리된 건입니다: {}", inId);
            return;
        }
        
        String materialId = (String) input.get("MATERIAL_ID");
        String productId = (String) input.get("PRODUCT_ID");
        String warehouseId = (String) input.get("WAREHOUSE_ID");
        Integer inCount = ((Number) input.get("IN_COUNT")).intValue();
        
        // 입고 상태 변경
        wareMapper.updateInputStatus(inId, "입고완료");
        
        if(productId != null) {
            // 완제품 처리
            wareMapper.updateProductQuantity(productId, inCount);
            String firstLocation = distributeToWarehouseItemsForProduct(warehouseId, productId, inCount, empId);
            
            if(firstLocation != null) {
                wareMapper.updateInputLocation(inId, firstLocation);
            }
        } else if(materialId != null) {
            // 부품/반제품 처리 (기존 코드)
            wareMapper.updateMaterialQuantity(materialId, inCount);
            String firstLocation = distributeToWarehouseItemsForMaterial(warehouseId, materialId, inCount, empId);
            
            if(firstLocation != null) {
                wareMapper.updateInputLocation(inId, firstLocation);
            }
        }
        
        log.info("입고 완료: {} - 수량: {}", inId, inCount);
        
        //	*******로트 생성: pk 값을 넘겨주는 곳**********
        HttpSession session = SessionUtil.getSession();
        session.setAttribute("targetIdValue", (String) input.get("IN_ID"));
    }
    
    // Material 재고 분산 저장 (내부 메서드)
    private String distributeToWarehouseItemsForMaterial(String warehouseId, String materialId, Integer totalCount, String empId) {
        String firstLocation = null;
        int remaining = totalCount;
        int maxPerLocation = 500;
        
        // 기존 위치 확인 (500개 미만)
        List<Map<String, Object>> existingItems = wareMapper.getPartiallyFilledLocationsMaterial(warehouseId, materialId, maxPerLocation);
        
        // 기존 위치 채우기
        for(Map<String, Object> item : existingItems) {
            if(remaining <= 0) break;
            
            String locationId = (String) item.get("locationId");
            Integer currentAmount = ((Number) item.get("itemAmount")).intValue();
            int availableSpace = maxPerLocation - currentAmount;
            int amountToAdd = Math.min(remaining, availableSpace);
            
            if(firstLocation == null) {
                firstLocation = locationId;
            }
            
            Map<String, Object> params = new HashMap<>();
            params.put("locationId", locationId);
            params.put("materialId", materialId);
            params.put("addAmount", amountToAdd);
            
            wareMapper.updateWarehouseItemAmountMaterial(params);
            
            remaining -= amountToAdd;
            log.info("기존 위치 {}에 {} 개 추가", locationId, amountToAdd);
        }
        
        // 새 위치 할당
        while(remaining > 0) {
            List<String> emptyLocations = wareMapper.getEmptyLocations(warehouseId);
            
            if(emptyLocations.isEmpty()) {
                log.warn("창고 {}에 빈 위치가 없습니다. 남은 수량: {}", warehouseId, remaining);
                break;
            }
            
            String locationId = emptyLocations.get(0);
            int amountToStore = Math.min(remaining, maxPerLocation);
            
            if(firstLocation == null) {
                firstLocation = locationId;
            }
            
            // 중복 확인 후 처리
            Map<String, Object> checkParams = new HashMap<>();
            checkParams.put("locationId", locationId);
            checkParams.put("materialId", materialId);
            checkParams.put("itemAmount", amountToStore);
            
            int updated = wareMapper.updateExistingMaterialLocation(checkParams);
            
            if(updated == 0) {
                // 신규 등록
                Map<String, Object> params = new HashMap<>();
                params.put("manageId", warehouseId + "_" + materialId + "_" + locationId);
                params.put("warehouseId", warehouseId);
                params.put("materialId", materialId);
                params.put("itemAmount", amountToStore);
                params.put("locationId", locationId);
                params.put("empId", empId);
                
                wareMapper.insertWarehouseItemMaterial(params);
            }
            
            remaining -= amountToStore;
            log.info("위치 {}에 {} 개 저장", locationId, amountToStore);
        }
        
        return firstLocation;
    }
    
    // 완제품 warehouse_item 분산 저장 메서드 추가
    private String distributeToWarehouseItemsForProduct(String warehouseId, String productId, Integer totalCount, String empId) {
        String firstLocation = null;
        int remaining = totalCount;
        int maxPerLocation = 500;
        
        // 기존 위치 확인
        List<Map<String, Object>> existingItems = wareMapper.getPartiallyFilledLocationsProduct(warehouseId, productId, maxPerLocation);
        
        // 기존 위치 채우기
        for(Map<String, Object> item : existingItems) {
            if(remaining <= 0) break;
            
            String locationId = (String) item.get("locationId");
            Integer currentAmount = ((Number) item.get("itemAmount")).intValue();
            int availableSpace = maxPerLocation - currentAmount;
            int amountToAdd = Math.min(remaining, availableSpace);
            
            if(firstLocation == null) {
                firstLocation = locationId;
            }
            
            Map<String, Object> params = new HashMap<>();
            params.put("locationId", locationId);
            params.put("productId", productId);
            params.put("addAmount", amountToAdd);
            
            wareMapper.updateWarehouseItemAmountProduct(params);
            
            remaining -= amountToAdd;
        }
        
        // 새 위치 할당
        while(remaining > 0) {
            List<String> emptyLocations = wareMapper.getEmptyLocations(warehouseId);
            
            if(emptyLocations.isEmpty()) {
                log.warn("창고 {}에 빈 위치가 없습니다. 남은 수량: {}", warehouseId, remaining);
                break;
            }
            
            String locationId = emptyLocations.get(0);
            int amountToStore = Math.min(remaining, maxPerLocation);
            
            if(firstLocation == null) {
                firstLocation = locationId;
            }
            
            Map<String, Object> params = new HashMap<>();
            params.put("manageId", warehouseId + "_" + productId + "_" + locationId);
            params.put("warehouseId", warehouseId);
            params.put("productId", productId);
            params.put("itemAmount", amountToStore);
            params.put("locationId", locationId);
            params.put("empId", empId);
            
            wareMapper.insertWarehouseItemProduct(params);
            
            remaining -= amountToStore;
        }
        
        return firstLocation;
    }
    
	// ==================== 출고 관리 ====================
	
    // 출고 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getOutputList(String outType, String outStatus, String startDate, String endDate) {
        return wareMapper.selectOutputList(outType, outStatus, startDate, endDate);
    }

    // 재고가 포함된 자재 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMaterialsWithStock() {
        return wareMapper.selectMaterialsWithStock();
    }

    // 재고가 포함된 완제품 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProductsWithStock() {
        return wareMapper.selectProductsWithStock();
    }
    
    // 배치 출고 등록 (통합 버전)
    @Transactional
    public String addOutputBatch(List<Map<String, Object>> items, String empId) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        Integer batchCount = wareMapper.getTodayOutputBatchCount(today);
        if(batchCount == null) batchCount = 0;

        String batchId = "OB" + today + String.format("%03d", batchCount + 1);

        for(Map<String, Object> item : items) {
            Integer outputCount = wareMapper.getTodayOutputCount(today);
            if(outputCount == null) outputCount = 0;

            String outId = "OUT" + today + String.format("%04d", outputCount + 1);

            item.put("outId", outId);
            item.put("batchId", batchId);
            item.put("empId", empId);
            item.put("outType", "출고완료");

            String productId = (String) item.get("productId");
            Integer outCount = Integer.parseInt(item.get("outCount").toString());

            // Material/Product 구분 처리 수정
            boolean isMaterial = wareMapper.checkIsMaterial(productId);
            if(isMaterial) {
                item.put("materialId", productId);
                item.put("productId", null);  // 빈 문자열 대신 null
            } else {
                item.put("productId", productId);
                item.put("materialId", null);  // 빈 문자열 대신 null
            }

            if(!checkStock(productId, outCount)) {
                throw new RuntimeException(productId + " 재고가 부족합니다.");
            }

            Map<String, Object> warehouseInfo = findAvailableWarehouseWithManage(productId);
            item.put("warehouseId", warehouseInfo.get("warehouseId"));
            item.put("manageId", warehouseInfo.get("manageId"));

            // 재고 차감 처리
            if(isMaterial) {
                reduceMaterialStock(productId, (String) warehouseInfo.get("warehouseId"), outCount);
            } else {
                reduceProductStock(productId, (String) warehouseInfo.get("warehouseId"), outCount);
            }

            wareMapper.insertOutput(item);
        }

        return batchId;
    }

    // 해당 품목의 재고가 있는 창고 정보(warehouseId, manageId) 조회
    private Map<String, Object> findAvailableWarehouseWithManage(String productId) {
        List<Map<String, Object>> warehouses = wareMapper.getWarehousesWithStock(productId);
        if(warehouses.isEmpty()) {
            throw new RuntimeException("재고가 있는 창고를 찾을 수 없습니다.");
        }
        
        String warehouseId = (String) warehouses.get(0).get("warehouseId");
        String manageId = wareMapper.getManageIdByWarehouse(productId, warehouseId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("warehouseId", warehouseId);
        result.put("manageId", manageId);
        
        return result;
    }

    // 출고 완료 처리
    @Transactional
    public void completeOutput(String outId, String empId) {
        Map<String, Object> output = wareMapper.selectOutputById(outId);
        
        if(output == null) {
            throw new RuntimeException("출고 정보를 찾을 수 없습니다.");
        }
        
        String currentType = (String) output.get("OUT_TYPE");  // OUT_STATUS → OUT_TYPE 변경
        if("출고완료".equals(currentType)) {
            log.info("이미 출고완료된 건입니다: {}", outId);
            return;
        }
        
        String productId = (String) output.get("PRODUCT_ID");
        String materialId = (String) output.get("MATERIAL_ID");  
        String warehouseId = (String) output.get("WAREHOUSE_ID");
        Integer outCount = ((Number) output.get("OUT_COUNT")).intValue();
        
        // 재고 차감 처리
        if(productId != null && !productId.isEmpty()) {  // 빈 문자열 체크 추가
            reduceProductStock(productId, warehouseId, outCount);
        } else if(materialId != null && !materialId.isEmpty()) {  // 빈 문자열 체크 추가
            reduceMaterialStock(materialId, warehouseId, outCount);
        }
        
        // 출고 상태 변경
        wareMapper.updateOutputType(outId, "출고완료");  
        
        log.info("출고 완료: {} - 수량: {}", outId, outCount);
    }

    // 재고 확인
    private boolean checkStock(String productId, Integer requiredQty) {
        boolean isMaterial = wareMapper.checkIsMaterial(productId);
        
        if(isMaterial) {
            Integer stock = wareMapper.getMaterialTotalStock(productId);
            return stock != null && stock >= requiredQty;
        } else {
            Integer stock = wareMapper.getProductTotalStock(productId);
            return stock != null && stock >= requiredQty;
        }
    }

    // Product 재고 차감 (warehouse_item 차감 후 product 차감)
    private void reduceProductStock(String productId, String warehouseId, Integer qty) {
        List<Map<String, Object>> locations = wareMapper.getProductStockLocations(productId, warehouseId);
        
        int remaining = qty;
        for(Map<String, Object> loc : locations) {
            if(remaining <= 0) break;
            
            String locationId = (String) loc.get("locationId");
            int currentQty = ((Number) loc.get("itemAmount")).intValue();
            
            int reduceQty = Math.min(remaining, currentQty);
            wareMapper.reduceWarehouseItemStock(productId, warehouseId, locationId, reduceQty);
            
            remaining -= reduceQty;
        }
        
        wareMapper.reduceProductQuantity(productId, qty);
    }

    // Material 재고 차감 (warehouse_item 차감 후 material 차감)
    private void reduceMaterialStock(String materialId, String warehouseId, Integer qty) {
        List<Map<String, Object>> locations = wareMapper.getMaterialStockLocations(materialId, warehouseId);
        
        int remaining = qty;
        for(Map<String, Object> loc : locations) {
            if(remaining <= 0) break;
            
            String locationId = (String) loc.get("locationId");
            int currentQty = ((Number) loc.get("itemAmount")).intValue();
            
            int reduceQty = Math.min(remaining, currentQty);
            wareMapper.reduceMaterialWarehouseStock(materialId, warehouseId, locationId, reduceQty);
            
            remaining -= reduceQty;
        }
        
        wareMapper.reduceMaterialQuantity(materialId, qty);
    }

    // 출고 취소
    @Transactional
    public void cancelOutput(String outId) {
        wareMapper.deleteOutput(outId);
    }
    
    // 배치별 출고 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getOutputListByBatch(String batchId) {
        return wareMapper.selectOutputListByBatch(batchId);
    }

    // 날짜별 배치 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getOutputBatches(String date, String outType) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);
        params.put("outType", outType);
        return wareMapper.selectOutputBatches(params);
    }
    
    // ==================== 데이터 조회 ====================

    // 부품 목록 조회 (구버전)
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPartsList() {
        return wareMapper.selectPartsList();
    }
    
    // 입고 가능한 Material 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMaterialsForInput() {
        return wareMapper.selectMaterialsForInput();
    }

    // 거래처 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getClientsList() {
        return wareMapper.selectClientsList();
    }
    
    // 완제품 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProductsForInput() {
        return wareMapper.selectProductsForInput();
    }
}