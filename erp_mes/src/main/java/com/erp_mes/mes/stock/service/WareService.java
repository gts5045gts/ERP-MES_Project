package com.erp_mes.mes.stock.service;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.stock.dto.WarehouseDTO;
import com.erp_mes.mes.stock.mapper.WareMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class WareService {
    
private final WareMapper wareMapper;
    
    // 창고 목록 조회
    @Transactional(readOnly = true)
    public List<WarehouseDTO> getWarehouseList(String warehouseType, String warehouseStatus, String searchKeyword) {
        log.info("창고 목록 조회 - 유형: {}, 상태: {}, 검색어: {}", warehouseType, warehouseStatus, searchKeyword);
        return wareMapper.selectWarehouseList(warehouseType, warehouseStatus, searchKeyword);
    }
    
    // 창고 등록
    @Transactional
    public void addWarehouse(WarehouseDTO dto) {
        log.info("창고 등록: {}", dto.getWarehouseId());
        
        if(wareMapper.existsWarehouseById(dto.getWarehouseId())) {
            throw new RuntimeException("이미 존재하는 창고ID입니다.");
        }
        
        wareMapper.insertWarehouse(dto);
    }
    
    // 창고 수정
    @Transactional
    public boolean updateWarehouse(WarehouseDTO dto) {
        log.info("창고 수정: {}", dto.getWarehouseId());
        return wareMapper.updateWarehouse(dto) > 0;
    }
    
    // 창고 삭제
    @Transactional
    public Map<String, Object> deleteWarehouses(List<String> warehouseIds) {
        log.info("창고 삭제 요청: {} 건", warehouseIds.size());
        
        List<String> canDelete = new ArrayList<>();
        List<String> cannotDelete = new ArrayList<>();
        
        // 각 창고에 재고가 있는지 체크
        for(String warehouseId : warehouseIds) {
            int inUseCount = wareMapper.checkWarehouseInUse(warehouseId);
            
            if(inUseCount > 0) {
                cannotDelete.add(warehouseId);
            } else {
                canDelete.add(warehouseId);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        
        // 삭제 가능한 창고만 삭제
        if(!canDelete.isEmpty()) {
            wareMapper.deleteWarehouses(canDelete);
            result.put("deleted", canDelete.size());
        }
        
        // 삭제 불가능한 창고 정보
        if(!cannotDelete.isEmpty()) {
            result.put("failed", cannotDelete);
            result.put("failedCount", cannotDelete.size());
        }
        
        result.put("success", cannotDelete.isEmpty());
        return result;
    }
    
    // 0917 입고 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getInputList(String inType, String inStatus) {
        return wareMapper.selectInputList(inType, inStatus);
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getInputListByBatch(String batchId) {
        return wareMapper.selectInputListByBatch(batchId);
    }

    // 입고 등록
    @Transactional
    public String addInput(Map<String, Object> params) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        Integer todayCount = wareMapper.getTodayInputCount(today);
        if(todayCount == null) todayCount = 0;
        
        String inId = "IN" + today + String.format("%03d", todayCount + 1);
        
        params.put("inId", inId);
        params.put("inStatus", "입고대기");  // 입고대기로 다시 변경!
        
        wareMapper.insertInput(params);
        
        log.info("입고 등록 완료: {}", inId);
        
        return inId;
    }

    // 입고 완료 처리
    @Transactional
    public void completeInput(String inId, String empId) {
        Map<String, Object> input = wareMapper.selectInputById(inId);
        
        if(input == null) {
            throw new RuntimeException("입고 정보를 찾을 수 없습니다.");
        }
        
        String currentStatus = (String) input.get("IN_STATUS");
        
        if(!"입고대기".equals(currentStatus)) {
            throw new RuntimeException("이미 처리된 입고입니다.");
        }
        
        String productId = (String) input.get("PRODUCT_ID");
        String warehouseId = (String) input.get("WAREHOUSE_ID");
        Integer inCount = ((Number) input.get("IN_COUNT")).intValue();
        
        // 1. 입고 상태를 '입고완료'로 변경
        wareMapper.updateInputStatus(inId, "입고완료");
        
        // 2. product 테이블 재고 증가
        wareMapper.updateProductQuantity(productId, inCount);
        
        // 3. warehouse_item 테이블에 재고 분산 저장
        distributeToWarehouseItems(warehouseId, productId, inCount, empId);
        
        log.info("입고 완료: {} - 수량: {}", inId, inCount);
    }
    
    // 창고 위치에 분산 저장
    private void distributeToWarehouseItems(String warehouseId, String productId, Integer totalCount, String empId) {
        int remaining = totalCount;
        int maxPerLocation = 500;
        
        // 1. 먼저 기존에 해당 제품이 있는 위치들 확인 (500개 미만인 곳)
        List<Map<String, Object>> existingItems = wareMapper.getPartiallyFilledLocations(warehouseId, productId, maxPerLocation);
        
        // 2. 기존 위치에 먼저 채우기
        for(Map<String, Object> item : existingItems) {
            if(remaining <= 0) break;
            
            String locationId = (String) item.get("locationId");
            Integer currentAmount = ((Number) item.get("itemAmount")).intValue();
            int availableSpace = maxPerLocation - currentAmount;
            int amountToAdd = Math.min(remaining, availableSpace);
            
            // 기존 위치에 추가
            Map<String, Object> params = new HashMap<>();
            params.put("locationId", locationId);
            params.put("productId", productId);
            params.put("addAmount", amountToAdd);
            
            wareMapper.updateWarehouseItemAmount(params);
            
            remaining -= amountToAdd;
            log.info("기존 위치 {}에 {} 개 추가 (기존: {}, 총: {})", 
                    locationId, amountToAdd, currentAmount, currentAmount + amountToAdd);
        }
        
        // 3. 남은 수량은 새 위치에 저장
        while(remaining > 0) {
            List<String> emptyLocations = wareMapper.getEmptyLocations(warehouseId);
            
            if(emptyLocations.isEmpty()) {
                log.warn("창고 {}에 빈 위치가 없습니다. 남은 수량: {}", warehouseId, remaining);
                break;
            }
            
            String locationId = emptyLocations.get(0);
            int amountToStore = Math.min(remaining, maxPerLocation);
            
            Map<String, Object> params = new HashMap<>();
            params.put("manageId", warehouseId + "_" + productId);
            params.put("warehouseId", warehouseId);
            params.put("productId", productId);
            params.put("itemAmount", amountToStore);
            params.put("locationId", locationId);
            params.put("empId", empId);
            
            wareMapper.insertWarehouseItem(params);
            
            remaining -= amountToStore;
            log.info("새 위치 {}에 {} 개 저장", locationId, amountToStore);
        }
    }

    // 부품 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPartsList() {
        return wareMapper.selectPartsList();
    }

    // 거래처 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getClientsList() {
        return wareMapper.selectClientsList();
    }

    // 창고 타입별 조회
    @Transactional(readOnly = true)
    public List<WarehouseDTO> getWarehouseListByType(String warehouseType) {
        return wareMapper.selectWarehouseListByType(warehouseType);
    }
    // 날짜별 그룹화된 입고 목록
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getGroupedInputList(String date, String inType) {
        return wareMapper.selectGroupedInputList(date, inType);
    }
    
    public Integer getTodayBatchCount(String today) {
        Integer count = wareMapper.getTodayBatchCount(today);
        return count != null ? count : 0;
    }
}