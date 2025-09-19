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
        
        // productId로 받은 값을 material_id로 저장
        String materialId = (String) params.get("productId");
        
        params.put("inId", inId);
        params.put("inStatus", "입고대기");
        params.put("materialId", materialId); 
        params.remove("productId");  
        
        wareMapper.insertInput(params);
        
        log.info("입고 등록 완료: {}", inId);
        
        return inId;
    }

    @Transactional
    @TrackLot(tableName = "input", pkColumnName = "IN_ID") // ******로트 관련 어노테이션****** 
    public void completeInput(String inId, String empId) {
        Map<String, Object> input = wareMapper.selectInputById(inId);
        
        if(input == null) {
            throw new RuntimeException("입고 정보를 찾을 수 없습니다.");
        }
        
        String currentStatus = (String) input.get("IN_STATUS");
        
        // 이미 완료된 건은 처리하지 않음
        if("입고완료".equals(currentStatus)) {
            log.info("이미 입고완료 처리된 건입니다: {}", inId);
            return;  // 에러 대신 그냥 리턴
        }
        
        String materialId = (String) input.get("MATERIAL_ID");
        String warehouseId = (String) input.get("WAREHOUSE_ID");
        Integer inCount = ((Number) input.get("IN_COUNT")).intValue();
        
        // 1. 입고 상태를 '입고완료'로 변경
        wareMapper.updateInputStatus(inId, "입고완료");
        
        // 2. material 테이블 재고 증가
        wareMapper.updateMaterialQuantity(materialId, inCount);
        
        // 3. warehouse_item 테이블에 재고 분산 저장하고 첫번째 위치 가져오기
        String firstLocation = distributeToWarehouseItemsForMaterial(warehouseId, materialId, inCount, empId);
        
        // 4. input 테이블의 location_id 업데이트 (전체 location_id 저장)
        if(firstLocation != null) {
            // 구역 추출 대신 전체 location_id 저장
            wareMapper.updateInputLocation(inId, firstLocation);
            log.info("위치 업데이트: {} -> {}", inId, firstLocation);
        }
        
        log.info("입고 완료: {} - 수량: {}", inId, inCount);
        
//		*******로트 생성: pk 값을 넘겨주는 곳**********
        HttpSession session = SessionUtil.getSession();
        session.setAttribute("targetIdValue", (String) input.get("IN_ID"));
    }
    
    // 구역 추출 메서드
    private String extractZone(String locationId) {
        if(locationId == null || locationId.length() < 6) {
            return locationId;
        }
        // DD02Z1R1L2C1 -> DD02Z1 (처음 6자리)
        return locationId.substring(0, 6);
    }
    
    // Material용 분산 저장 메서드 추가
    private String distributeToWarehouseItemsForMaterial(String warehouseId, String materialId, Integer totalCount, String empId) {
        String firstLocation = null;
        int remaining = totalCount;
        int maxPerLocation = 500;
        
        // 1. 기존 위치 확인
        List<Map<String, Object>> existingItems = wareMapper.getPartiallyFilledLocationsMaterial(warehouseId, materialId, maxPerLocation);
        
        // 2. 기존 위치에 채우기
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
        
        // 3. 새 위치에 저장
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
            
            // 먼저 해당 위치에 이미 있는지 확인
            Map<String, Object> checkParams = new HashMap<>();
            checkParams.put("locationId", locationId);
            checkParams.put("materialId", materialId);
            checkParams.put("itemAmount", amountToStore);
            
            // 이미 있으면 업데이트, 없으면 신규 생성
            int updated = wareMapper.updateExistingMaterialLocation(checkParams);
            
            if(updated == 0) {
                // 신규 생성
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
    
    //0919
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMaterialsForInput() {
        return wareMapper.selectMaterialsForInput();
    }
}