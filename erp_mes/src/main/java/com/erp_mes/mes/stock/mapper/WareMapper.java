package com.erp_mes.mes.stock.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.erp_mes.mes.stock.dto.WarehouseDTO;

@Mapper
public interface WareMapper {
    
    // ==================== 창고 관리 ====================
    
    // 창고 목록 조회 (검색 조건 포함)
    List<WarehouseDTO> selectWarehouseList(@Param("warehouseType") String warehouseType,
                                           @Param("warehouseStatus") String warehouseStatus,
                                           @Param("searchKeyword") String searchKeyword);
    
    // 창고 ID 중복 확인
    boolean existsWarehouseById(@Param("warehouseId") String warehouseId);
    
    // 신규 창고 등록
    int insertWarehouse(WarehouseDTO dto);
    
    // 창고 정보 수정
    int updateWarehouse(WarehouseDTO dto);
    
    // 창고 삭제 (다중 선택)
    int deleteWarehouses(@Param("list") List<String> warehouseIds);
    
    // 창고 내 재고 존재 여부 확인
    int checkWarehouseInUse(@Param("warehouseId") String warehouseId);
    
    // 특정 타입 창고 목록 조회
    List<WarehouseDTO> selectWarehouseListByType(@Param("warehouseType") String warehouseType);
    
    // ==================== 입고 관리 ====================
    
    // 입고 목록 조회
    List<Map<String, Object>> selectInputList(@Param("inType") String inType, 
                                              @Param("inStatus") String inStatus);
    
    // 날짜별 그룹화된 입고 목록 조회
    List<Map<String, Object>> selectGroupedInputList(@Param("date") String date, 
                                                     @Param("inType") String inType);
    
    // 배치별 입고 목록 조회
    List<Map<String, Object>> selectInputListByBatch(@Param("batchId") String batchId);
    
    // 입고 상세 정보 조회
    Map<String, Object> selectInputById(@Param("inId") String inId);
    
    // 오늘 입고 건수 조회
    Integer getTodayInputCount(@Param("today") String today);
    
    // 오늘 배치 건수 조회
    Integer getTodayBatchCount(@Param("today") String today);
    
    // 신규 입고 등록
    int insertInput(Map<String, Object> params);
    
    // 입고 상태 변경
    int updateInputStatus(@Param("inId") String inId, @Param("status") String status);
    
    // 입고 위치 정보 업데이트
    int updateInputLocation(@Param("inId") String inId, @Param("locationId") String locationId);
    
    // ==================== 재고 처리 ====================
    
    // 재고 증가 처리 (구버전)
    int increaseStock(@Param("warehouseId") String warehouseId,
                      @Param("productId") String productId,
                      @Param("locationId") String locationId,
                      @Param("inCount") Integer inCount);
    
    // Product 재고 수량 증가
    int updateProductQuantity(@Param("productId") String productId, 
                             @Param("inCount") Integer inCount);
    
    // Material 재고 수량 증가
    int updateMaterialQuantity(@Param("materialId") String materialId, 
                              @Param("inCount") Integer inCount);
    
    // Product 관련 warehouse_item 메서드 추가
    List<Map<String, Object>> getPartiallyFilledLocationsProduct(@Param("warehouseId") String warehouseId,
                                                                 @Param("productId") String productId,
                                                                 @Param("maxAmount") Integer maxAmount);
    
    int updateWarehouseItemAmountProduct(Map<String, Object> params);

    int insertWarehouseItemProduct(Map<String, Object> params);

    // ==================== 창고 위치 관리 ====================
    
    // 창고 내 빈 위치 조회
    List<String> getEmptyLocations(@Param("warehouseId") String warehouseId);
    
    // 500개 미만 채워진 위치 조회 (Product)
    List<Map<String, Object>> getPartiallyFilledLocations(@Param("warehouseId") String warehouseId,
                                                          @Param("productId") String productId,
                                                          @Param("maxAmount") Integer maxAmount);
    
    // 500개 미만 채워진 위치 조회 (Material)
    List<Map<String, Object>> getPartiallyFilledLocationsMaterial(@Param("warehouseId") String warehouseId,
                                                                  @Param("materialId") String materialId,
                                                                  @Param("maxAmount") Integer maxAmount);
    
    // ==================== Warehouse_Item 처리 ====================
    
    // warehouse_item 등록 또는 업데이트
    int insertOrUpdateWarehouseItem(Map<String, Object> params);
    
    // warehouse_item 신규 등록
    int insertWarehouseItem(Map<String, Object> params);
    
    // warehouse_item 신규 등록 (Material)
    int insertWarehouseItemMaterial(Map<String, Object> params);
    
    // warehouse_item 수량 증가
    int updateWarehouseItemAmount(Map<String, Object> params);
    
    // warehouse_item 수량 증가 (Material)
    int updateWarehouseItemAmountMaterial(Map<String, Object> params);
    
    // 기존 Material 위치 업데이트 (중복 방지)
    int updateExistingMaterialLocation(Map<String, Object> params);
    
    // ==================== 기초 데이터 조회 ====================
    
    // 부품 목록 조회 (구버전)
    List<Map<String, Object>> selectPartsList();
    
    // 입고 가능한 Material 목록 조회
    List<Map<String, Object>> selectMaterialsForInput();
    
    // 거래처 목록 조회
    List<Map<String, Object>> selectClientsList();
    
    // 생산후 입고 가능한 Product 목록 조회 (완제품)
    List<Map<String, Object>> selectProductsForInput();
}