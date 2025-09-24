package com.erp_mes.mes.stock.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
    public String addInput(Map<String, Object> params) {
        String itemType = (String) params.get("itemType");
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        Integer todayCount = wareMapper.getTodayInputCount(today);
        if(todayCount == null) todayCount = 0;

        String inId = "IN" + today + String.format("%03d", todayCount + 1);

        params.put("inId", inId);

        // 사유가 없으면 입고타입으로 설정
        if(params.get("inReason") == null || "".equals(params.get("inReason"))) {
            params.put("inReason", params.get("inType"));
        }

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
        
        // 위치 할당 먼저 시도
        String firstLocation = null;
        
        if(productId != null) {
            // 완제품 처리
            firstLocation = distributeToWarehouseItemsForProduct(warehouseId, productId, inCount, empId);
        } else if(materialId != null) {
            // 부품/반제품 처리
            firstLocation = distributeToWarehouseItemsForMaterial(warehouseId, materialId, inCount, empId);
        }
        
        // 위치 할당 실패 시 에러 처리
        if(firstLocation == null) {
            throw new RuntimeException(
                String.format("창고 %s에 충분한 저장 공간이 없습니다. 입고 수량: %d개", 
                             warehouseId, inCount)
            );
        }
        
        // 위치 할당 성공한 경우에만 입고 완료 처리
        wareMapper.updateInputStatus(inId, "입고완료");
        
        if(productId != null) {
            wareMapper.updateProductQuantity(productId, inCount);
        } else if(materialId != null) {
            wareMapper.updateMaterialQuantity(materialId, inCount);
        }
        
        wareMapper.updateInputLocation(inId, firstLocation);
        
        log.info("입고 완료: {} - 수량: {}, 첫 위치: {}", inId, inCount, firstLocation);
        
        HttpSession session = SessionUtil.getSession();
        session.setAttribute("targetIdValue", inId);
    }
    
    
    
    private String distributeToWarehouseItemsForMaterial(String warehouseId, String materialId, Integer totalCount, String empId) {
        String firstLocation = null;
        int remaining = totalCount;
        int maxPerLocation = 500;
        int maxLocationsPerMaterial = 6;
        
        // 현재 이 material이 사용 중인 위치 수 확인
        int currentLocationCount = wareMapper.getLocationCountForMaterial(warehouseId, materialId);
        
        while(remaining > 0) {
            // 재사용 가능한 위치 확인 (item_amount = 0인 곳)
            List<String> reusableLocations = wareMapper.getReusableLocations(warehouseId, materialId);
            
            if(!reusableLocations.isEmpty()) {
                // 기존 코드 그대로 (재사용 로직)
                for(String locationId : reusableLocations) {
                    if(remaining <= 0) break;
                    
                    int amountToStore = Math.min(remaining, maxPerLocation);
                    
                    if(firstLocation == null) {
                        firstLocation = locationId;
                    }
                    
                    Map<String, Object> params = new HashMap<>();
                    params.put("locationId", locationId);
                    params.put("materialId", materialId);
                    params.put("itemAmount", amountToStore);
                    
                    wareMapper.updateWarehouseItemAmountMaterial(params);
                    
                    remaining -= amountToStore;
                    log.info("기존 위치 {} 재사용, {} 개로 업데이트", locationId, amountToStore);
                }
            }
            
            // 재사용 위치가 없거나 부족하면 새 위치에 INSERT
            if(remaining > 0) {
                // 6개 제한 체크
                if(currentLocationCount >= maxLocationsPerMaterial) {
                    throw new RuntimeException(
                        String.format("자재 %s는 최대 %d개 위치까지만 사용 가능합니다", 
                            materialId, maxLocationsPerMaterial)
                    );
                }
                
                List<String> emptyLocations = wareMapper.getEmptyLocations(warehouseId);
                
                // *** 여기가 핵심 수정 부분 ***
                if(emptyLocations.isEmpty()) {
                    // 빈 위치가 없으면 자동 생성
                    String newLocationId = String.format("%s-%s-%02d", 
                        warehouseId, 
                        materialId.length() > 4 ? materialId.substring(materialId.length()-4) : materialId,
                        currentLocationCount + 1
                    );
                    
                    // item_location 테이블에 새 위치 추가
                    Map<String, Object> locParams = new HashMap<>();
                    locParams.put("warehouseId", warehouseId);
                    locParams.put("locationId", newLocationId);
                    locParams.put("locZone", "AUTO");
                    locParams.put("locRack", String.format("%02d", currentLocationCount + 1));
                    locParams.put("locLevel", "01");
                    locParams.put("locCell", "01");
                    locParams.put("zoneYn", "Y");
                    locParams.put("empId", empId);
                    
                    wareMapper.insertItemLocation(locParams);
                    log.info("자재 {}용 새 위치 {} 자동 생성", materialId, newLocationId);
                    
                    // 생성한 위치를 리스트에 추가
                    emptyLocations = Arrays.asList(newLocationId);
                }
                
                // 빈 위치들 중에서 INSERT 시도 (기존 코드 그대로)
                boolean inserted = false;
                for(String locationId : emptyLocations) {
                    int amountToStore = Math.min(remaining, maxPerLocation);
                    
                    if(firstLocation == null) {
                        firstLocation = locationId;
                    }
                    
                    Map<String, Object> params = new HashMap<>();
                    params.put("manageId", warehouseId + "_" + materialId + "_" + locationId);
                    params.put("warehouseId", warehouseId);
                    params.put("materialId", materialId);
                    params.put("itemAmount", amountToStore);
                    params.put("locationId", locationId);
                    params.put("empId", empId);
                    
                    int result = wareMapper.insertWarehouseItemMaterial(params);
                    
                    if(result > 0) {
                        remaining -= amountToStore;
                        currentLocationCount++;  // 사용 위치 수 증가
                        log.info("새 위치 {}에 {} 개 저장", locationId, amountToStore);
                        inserted = true;
                        break;
                    }
                }
                if(!inserted) {
                    throw new RuntimeException("창고에 저장 가능한 위치가 없습니다");
                }
            }
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
    
    // 입고 반려 처리
    @Transactional
    public void rejectInput(String inId, String reason, String empId) {
        Map<String, Object> input = wareMapper.selectInputById(inId);
        
        if(input == null) {
            throw new RuntimeException("입고 정보를 찾을 수 없습니다.");
        }
        
        String status = (String) input.get("IN_STATUS");
        
        // 입고대기 상태만 반려 가능
        if(!"입고대기".equals(status)) {
            throw new RuntimeException("입고대기 상태에서만 반려할 수 있습니다.");
        }
        
        // 입고 상태를 입고반려로 변경
        wareMapper.updateInputStatusWithReason(inId, "입고반려", reason);
        
        log.info("입고 반려 처리: {} (사유: {}, 처리자: {})", inId, reason, empId);
    }
    
    // 반려 사유 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRejectReasons() {
        return wareMapper.selectRejectReasons();
    }
    
    // 생산 완료 제품 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCompletedProduction(String date) {
        List<Map<String, Object>> result = wareMapper.selectTodayProductionForInput(date);
        log.info("생산 완료 조회 결과: {}", result.size());
        return result;
    }
    
    @Transactional
    public String addProductionBatch(List<Map<String, Object>> items, String empId) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        Integer batchCount = wareMapper.getTodayBatchCount(today);
        String batchId = "PB" + today + String.format("%03d", batchCount + 1);
        
        log.info("생산 배치 입고 시작: {} 건", items.size());
        
        for(Map<String, Object> item : items) {
            try {
                // Integer를 String으로 안전하게 변환
                Object resultIdObj = item.get("resultId");
                String resultId = resultIdObj != null ? String.valueOf(resultIdObj) : null;
                
                log.info("처리 중: resultId={}", resultId);
                
                item.put("empId", empId);
                item.put("batchId", batchId);
                
                String inId = addInput(item);
                log.info("입고 완료: inId={}", inId);
                
                if(resultId != null && !"".equals(resultId)) {
                    log.info("work_result 업데이트: resultId={}, inId={}", resultId, inId);
                    wareMapper.updateWorkResultInId(resultId, inId);
                }
            } catch(Exception e) {
                log.error("입고 처리 중 에러:", e);
                throw e;
            }
        }
        
        return batchId;
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
        // 기존 배치ID 생성 코드 사용
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        Integer batchCount = wareMapper.getTodayOutputBatchCount(today);
        if(batchCount == null) batchCount = 0;
        String batchId = "OB" + today + String.format("%03d", batchCount + 1);
        
        for(Map<String, Object> item : items) {
            String productId = (String) item.get("productId");
            Integer totalQty = Integer.parseInt(item.get("outCount").toString());
            
            // Material인지 확인
            boolean isMaterial = wareMapper.checkIsMaterial(productId);
            
            // 재고 위치 조회
            List<Map<String, Object>> locations;
            if(isMaterial) {
                locations = wareMapper.getAllMaterialLocations(productId);
            } else {
                locations = wareMapper.getAllProductLocations(productId);  
            }
            
            int remaining = totalQty;
            for(Map<String, Object> loc : locations) {
                if(remaining <= 0) break;
                
                // 출고ID 생성
                Integer outputCount = wareMapper.getTodayOutputCount(today);
                if(outputCount == null) outputCount = 0;
                String outId = "OUT" + today + String.format("%04d", outputCount + 1);
                
                Integer stockQty = ((Number) loc.get("itemAmount")).intValue();
                Integer outQty = Math.min(remaining, stockQty);
                
                // 각 위치별로 output 행 생성
                Map<String, Object> outItem = new HashMap<>();
                outItem.put("outId", outId);
                outItem.put("batchId", batchId);
                outItem.put("warehouseId", loc.get("warehouseId"));
                outItem.put("manageId", loc.get("manageId"));
                outItem.put("locationId", loc.get("locationId"));
                outItem.put("outCount", outQty);
                outItem.put("empId", empId);
                outItem.put("outType", "출고완료");
                
                if(isMaterial) {
                    outItem.put("materialId", productId);
                    // warehouse_item 재고 차감
                    wareMapper.reduceMaterialWarehouseStock(productId, 
                        (String)loc.get("warehouseId"), 
                        (String)loc.get("locationId"), 
                        outQty);
                } else {
                    outItem.put("productId", productId);
                    // warehouse_item 재고 차감
                    wareMapper.reduceWarehouseItemStock(productId, 
                        (String)loc.get("warehouseId"), 
                        (String)loc.get("locationId"), 
                        outQty);
                }
                wareMapper.insertOutput(outItem);
                remaining -= outQty;
            }
            // 전체 수량 차감
            if(isMaterial) {
                wareMapper.reduceMaterialQuantity(productId, totalQty);
            } else {
                wareMapper.reduceProductQuantity(productId, totalQty);
            }
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
    
    // Material 재고 차감 (warehouse_item 기반)
    @Transactional
    public boolean reduceMtlStock(String materialId, String warehouseId, 
            String locationId, Integer reduceQty, String reason, String empId) {
        
        // 전체 창고에서 차감
        List<Map<String, Object>> allLocations = wareMapper.getAllMaterialLocations(materialId);
        
        if(allLocations.isEmpty()) {
            throw new RuntimeException("재고가 없습니다.");
        }
        
        int remaining = reduceQty;
        String firstWarehouseId = null;
        String firstManageId = null;  
        
        for(Map<String, Object> loc : allLocations) {
            if(remaining <= 0) break;
            
            String whId = (String) loc.get("warehouseId");
            String locId = (String) loc.get("locationId");
            String manageId = (String) loc.get("manageId"); 
            int currentQty = ((Number) loc.get("itemAmount")).intValue();
            
            if(firstWarehouseId == null) {
                firstWarehouseId = whId;
                firstManageId = manageId; 
            }
            
            int reduceAmt = Math.min(remaining, currentQty);
            
            if(reduceAmt == currentQty) {
                wareMapper.deleteMtlStock(materialId, whId, locId);
            } else {
                wareMapper.updateMtlStock(materialId, whId, locId, currentQty - reduceAmt);
            }
            
            remaining -= reduceAmt;
        }
        
        if(remaining > 0) {
            throw new RuntimeException("재고 부족");
        }
        
        // material 테이블 동기화
        wareMapper.syncMaterialQty(materialId);
        
        // 출고 기록 생성
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        Integer count = wareMapper.getTodayOutputCount(today);
        if(count == null) count = 0;
        
        String outId = "OUT" + today + String.format("%04d", count + 1);
        
        Map<String, Object> params = new HashMap<>();
        params.put("outId", outId);
        params.put("materialId", materialId);
        params.put("warehouseId", firstWarehouseId);
        params.put("locationId", "AUTO");
        params.put("manageId", firstManageId);
        params.put("outCount", reduceQty);
        params.put("outType", "출고완료");
        params.put("empId", empId);
        params.put("batchId", "MI" + today + String.format("%03d", count + 1));
        params.put("outRemark", reason);
        
        wareMapper.insertOutput(params);
        
        return true;
    }
    
    // 출고 내역 삭제
    @Transactional
    public int deleteOutputs(List<String> outIds) {
        int count = 0;
        for(String outId : outIds) {
            wareMapper.deleteOutput(outId);
            count++;
        }
        log.info("출고 내역 삭제: {}건", count);
        return count;
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