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
	
	// 반려 사유 코드 조회
	List<Map<String, Object>> selectRejectReasons();
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
	// Product warehouse_item 수량 업데이트
	int updateWarehouseItemAmountProduct(Map<String, Object> params);
	
	// Product warehouse_item 등록
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
	
	// item_location 테이블에 새 위치 추가 (이거 추가!)
    int insertItemLocation(Map<String, Object> params);
	
	// warehouse_item 수량 증가
	int updateWarehouseItemAmount(Map<String, Object> params);
	
	// warehouse_item 수량 증가 (Material)
	int updateWarehouseItemAmountMaterial(Map<String, Object> params);
	
	// 기존 Material 위치 업데이트 (중복 방지)
	int updateExistingMaterialLocation(Map<String, Object> params);
	
	// 0924 input 테이블 manage_id 업데이트
    int updateInputManageId(@Param("inId") String inId, 
                           @Param("manageId") String manageId);
	
	// ==================== 출고 관리 ====================
	
	// 출고 목록 조회
	List<Map<String, Object>> selectOutputList(@Param("outType") String outType,
	                                            @Param("outStatus") String outStatus,
	                                            @Param("startDate") String startDate,
	                                            @Param("endDate") String endDate);
	
	// 오늘 출고 건수
	Integer getTodayOutputCount(@Param("today") String today);
	
	// 오늘 출고 배치 건수
	Integer getTodayOutputBatchCount(@Param("today") String today);
	
	// 출고 등록
	int insertOutput(Map<String, Object> params);
	
	// 출고 정보 조회
	Map<String, Object> selectOutputById(@Param("outId") String outId);
	
	List<Map<String, Object>> getAllProductLocations(@Param("productId") String productId);
	
	// material이 사용 중인 위치 개수 조회
	int getLocationCountForMaterial(@Param("warehouseId") String warehouseId, 
	                                @Param("materialId") String materialId);

	// item_amount가 0인 재사용 가능한 위치 조회
	List<String> getReusableLocations(@Param("warehouseId") String warehouseId, 
	                                  @Param("materialId") String materialId);
	
	// 출고 상태 변경
	int updateOutputType(@Param("outId") String outId, @Param("type") String type);
	
	// Material인지 확인
	boolean checkIsMaterial(@Param("productId") String productId);
	
	// Material 전체 재고
	Integer getMaterialTotalStock(@Param("materialId") String materialId);
	
	// Product 전체 재고
	Integer getProductTotalStock(@Param("productId") String productId);
	
	// 재고가 있는 창고 조회
	List<Map<String, Object>> getWarehousesWithStock(@Param("productId") String productId);
	
	// Product 재고 위치 조회
	List<Map<String, Object>> getProductStockLocations(@Param("productId") String productId,
	                                                    @Param("warehouseId") String warehouseId);
	
	// Material 재고 위치 조회
	List<Map<String, Object>> getMaterialStockLocations(@Param("materialId") String materialId,
	                                                     @Param("warehouseId") String warehouseId);

	// warehouse_item 재고 차감
	int reduceWarehouseItemStock(@Param("productId") String productId,
                              @Param("warehouseId") String warehouseId,
                              @Param("locationId") String locationId,
                              @Param("qty") Integer qty);
	// Material warehouse_item 재고 차감
	int reduceMaterialWarehouseStock(@Param("materialId") String materialId,
	                          @Param("warehouseId") String warehouseId,
	                          @Param("locationId") String locationId,
	                          @Param("qty") Integer qty);
	 
	// manage_id 조회
	String getManageIdByWarehouse(@Param("productId") String productId, 
	     					   @Param("warehouseId") String warehouseId);
	
	// Product 수량 감소
	int reduceProductQuantity(@Param("productId") String productId, @Param("qty") Integer qty);
	
	// Material 수량 감소
	int reduceMaterialQuantity(@Param("materialId") String materialId, @Param("qty") Integer qty);
	
	// 출고 삭제
	int deleteOutput(@Param("outId") String outId);
	
	// 재고 포함 자재 목록 조회
	List<Map<String, Object>> selectMaterialsWithStock();
	
	// 재고 포함 완제품 목록 조회  
	List<Map<String, Object>> selectProductsWithStock();
	 
	// 출고 배치 조회
	List<Map<String, Object>> selectOutputBatches(@Param("date") String date);
	 
	// 배치별 출고 상세 목록 조회
	List<Map<String, Object>> selectOutputListByBatch(@Param("batchId") String batchId);
	
	// 배치 목록 조회 (파라미터 맵 방식)
	List<Map<String, Object>> selectOutputBatches(Map<String, Object> params);
	
	// 0923 Material 재고 조회
	int getMtlStockQty(@Param("materialId") String materialId,
	                   @Param("warehouseId") String warehouseId,
	                   @Param("locationId") String locationId);

	// Material warehouse_item 업데이트
	int updateMtlStock(@Param("materialId") String materialId,
	                   @Param("warehouseId") String warehouseId,
	                   @Param("locationId") String locationId,
	                   @Param("newQty") Integer newQty);

	// Material warehouse_item 삭제
	int deleteMtlStock(@Param("materialId") String materialId,
	                   @Param("warehouseId") String warehouseId,
	                   @Param("locationId") String locationId);

	// Material 테이블 수량 동기화
	int syncMaterialQty(@Param("materialId") String materialId);
	
	List<Map<String, Object>> getAllMaterialLocations(@Param("materialId") String materialId);
	
	// 생산 완료 제품 조회
	List<Map<String, Object>> selectTodayProductionForInput(@Param("date") String date);

	// work_result in_id 업데이트
	int updateWorkResultInId(@Param("resultId") String resultId, @Param("inId") String inId);
	
	List<Map<String, Object>> getMaterialStockGroupByManageId(@Param("materialId") String materialId);
	
	// manage_id로 재고 수량 조회
	Integer getMaterialStockByManageId(@Param("manageId") String manageId);
	
	// LOT 마스터 등록
	int insertLotMaster(Map<String, Object> params);
	
	// 0925 manage_id별 출고 시퀀스 조회
    Integer getOutputSeqByManageId(@Param("manageId") String manageId, 
                                   @Param("today") String today);
    
    // 0926 수주 대기 목록 조회
    List<Map<String, Object>> selectPendingOrders();

    // 수주 상세 조회
    List<Map<String, Object>> selectOrderDetails(@Param("orderId") String orderId);

    // 수주 상세 출하수량 업데이트
    int updateOrderDetailShipped(@Param("orderId") String orderId,
                                @Param("productId") String productId,
                                @Param("shippedQty") Integer shippedQty);

    // 수주 상태 업데이트
    int updateOrderStatus(@Param("orderId") String orderId,
                         @Param("status") String status);

    // 오늘 완제품 출고 배치 건수 (POB 접두사용)
    Integer getTodayProductOutputBatchCount(@Param("today") String today);
    
    // 완제품 manage_id별 재고 조회
    List<Map<String, Object>> getProductStockGroupByManageId(@Param("productId") String productId);
    
    // 특정 접두사로 시작하는 out_id의 최대 번호 조회
    Integer getMaxOutputCount(@Param("prefix") String prefix);
    
    // work_order_id 업데이트
    int updateOutputWorkOrder(@Param("outId") String outId, 
                             @Param("workOrderId") Integer workOrderId);
    
    // 생산계획 관련 메서드 추가
    List<Map<String, Object>> selectPendingProductPlans();
    List<Map<String, Object>> selectPlanBOMDetails(@Param("planId") String planId);

    // 생산계획 기반 출고 배치 처리
    int insertProductionOutputBatch(Map<String, Object> params);

    // output 테이블에 plan_id 업데이트
    int updateOutputPlanId(@Param("outId") String outId, @Param("planId") String planId);
    
    // ==================== 기초 데이터 조회 ====================
    
    // 부품 목록 조회 (구버전)
    List<Map<String, Object>> selectPartsList();
    
    // 입고 가능한 Material 목록 조회
    List<Map<String, Object>> selectMaterialsForInput();
    
    // 거래처 목록 조회
    List<Map<String, Object>> selectClientsList();
    
    // 생산후 입고 가능한 Product 목록 조회 (완제품)
    List<Map<String, Object>> selectProductsForInput();
    
    // 0922 입고 상태 변경 (사유 포함)
    int updateInputStatusWithReason(@Param("inId") String inId, 
                                    @Param("status") String status,
                                    @Param("reason") String reason);

    // 입고 정보 조회 (사유 포함)
    Map<String, Object> selectInputWithReason(@Param("inId") String inId);
    
 // 발주 대기 목록 조회
    List<Map<String, Object>> selectPendingPurchases();
    
    // 발주 상세 조회
    List<Map<String, Object>> selectPurchaseDetails(@Param("purId") String purId);
    
    // 특정 품목의 발주 상세 조회
    Map<String, Object> selectPurchaseDetailByMaterial(@Param("purId") String purId, 
                                                      @Param("materialId") String materialId);
    
    // 발주 상태 업데이트
    int updatePurchaseStatus(@Param("purId") String purId, 
                            @Param("status") String status);
    
    // 발주 상세 상태 업데이트
    int updatePurchaseDetailStatus(@Param("purId") String purId,
                                  @Param("materialId") String materialId,
                                  @Param("status") String status);
    
    // ====================================================
 // 창고 단일 조회
    WarehouseDTO selectWarehouseInfoById(@Param("warehouseId") String warehouseId);
    
    // 창고 재고 레이아웃 조회
    List<Map<String, Object>> selectWarehouseStockLayout(@Param("warehouseId") String warehouseId);
    
    // 위치 상세 정보 조회
    Map<String, Object> selectLocationDetail(@Param("locationId") String locationId);
    
    // 창고 내 총 위치 수
    int getTotalLocations(@Param("warehouseId") String warehouseId);
    
    // 창고 내 사용중인 위치 수  
    int getUsedLocations(@Param("warehouseId") String warehouseId);
    
    // 창고 내 빈 위치 수 (기존 메서드 오버로딩)
    int getEmptyLocationCnt(@Param("warehouseId") String warehouseId);
    
    // 창고 내 총 재고량
    int getTotalStockInWarehouse(@Param("warehouseId") String warehouseId);
    
    // 위치별 입출고 이력 조회
    List<Map<String, Object>> selectLocationHistory(
        @Param("locationId") String locationId,
        @Param("limit") int limit
    );
    
    // manage_id로 재고 이력 조회
    List<Map<String, Object>> selectStockHistoryByManageId(
        @Param("manageId") String manageId,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate
    );
    
    // 특정 품목의 모든 위치 조회
    List<Map<String, Object>> selectAllLocationsByItem(
        @Param("itemId") String itemId,
        @Param("itemType") String itemType  // 'material' or 'product'
    );
    
    // 창고별 구역 목록 조회
    List<String> selectZonesByWarehouse(@Param("warehouseId") String warehouseId);
    
    // 구역별 랙 목록 조회
    List<String> selectRacksByZone(
        @Param("warehouseId") String warehouseId,
        @Param("zone") String zone
    );
    
    // 재고 조정 (임시 - 실제는 입출고 프로세스 거쳐야 함)
    int updateStockQuantity(
        @Param("manageId") String manageId,
        @Param("newQty") int newQty,
        @Param("empId") String empId
    );
    
    //======================================================== 
    // 미착수 work_order 조회 메서드 추가
    Map<String, Object> getUnstartedWorkOrder(@Param("planId") String planId);
}