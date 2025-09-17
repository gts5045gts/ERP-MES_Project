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
        log.info("입고 목록 조회 - 타입: {}, 상태: {}", inType, inStatus);
        return wareMapper.selectInputList(inType, inStatus);
    }

    // 입고 등록
    @Transactional
    public String addInput(Map<String, Object> params) {
        // 입고ID 생성
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        Integer todayCount = wareMapper.getTodayInputCount(today);
        String inId = "IN" + today + String.format("%03d", todayCount + 1);
        
        params.put("inId", inId);
        params.put("lotId", "LOT" + today + params.get("productId"));
        params.put("inStatus", "입고대기");
        params.put("manageId", params.get("warehouseId") + "_" + params.get("productId"));
        
        // 빈 위치 자동 할당
        String warehouseId = (String) params.get("warehouseId");
        List<String> emptyLocations = wareMapper.getEmptyLocations(warehouseId);
        if(!emptyLocations.isEmpty()) {
            params.put("locationId", emptyLocations.get(0));
        }
        
        wareMapper.insertInput(params);
        
        log.info("입고 등록 완료: {}", inId);
        return inId;
    }

    // 입고 완료 처리
    @Transactional
    public void completeInput(String inId, String empId) {
        Map<String, Object> input = wareMapper.selectInputById(inId);
        
        if(!"입고대기".equals(input.get("inStatus"))) {
            throw new RuntimeException("이미 처리된 입고입니다.");
        }
        
        // 입고 상태 업데이트
        wareMapper.updateInputStatus(inId, "입고완료");
        
        // 재고 증가 처리
        String productId = (String) input.get("productId");
        String warehouseId = (String) input.get("warehouseId");
        String locationId = (String) input.get("locationId");
        Integer inCount = ((Number) input.get("inCount")).intValue();
        
        // warehouse_item에서 해당 위치의 재고 증가
        wareMapper.increaseStock(warehouseId, productId, locationId, inCount);
        
        log.info("입고 완료: {} - 수량: {}", inId, inCount);
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
}