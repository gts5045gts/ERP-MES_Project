package com.erp_mes.mes.stock.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.mes.stock.dto.ProductDTO;
import com.erp_mes.mes.stock.dto.MaterialDTO;
import com.erp_mes.mes.stock.dto.StockDTO;
import com.erp_mes.mes.stock.dto.WarehouseDTO;

@Mapper
public interface StockMapper {
    
    // ==================== 재고 현황 관련 ====================
    
    // 전체 재고 목록 조회 (material + product 통합)
    List<StockDTO> getAllStockList(@Param("productName") String productName, 
                                   @Param("warehouseId") String warehouseId);
    
    // 재고 목록 조회 (기존 product만)
    List<StockDTO> getStockList(@Param("productName") String productName, 
                                @Param("warehouseId") String warehouseId);
    
    // 창고 목록 조회
    List<WarehouseDTO> getWarehouseList();
    
    // 재고 상세 정보 조회
    StockDTO getStockDetail(@Param("productId") String productId);
    
    // 재고 수량 직접 업데이트
    int updateStockAmount(@Param("productId") String productId, 
                         @Param("warehouseId") String warehouseId,
                         @Param("itemAmount") Integer itemAmount);
    
    // Material 재고 차감 처리 (생산 투입용)
    int reduceMaterialStock(@Param("materialId") String materialId, 
                           @Param("reduceQty") Integer reduceQty);
    
    // Material의 창고별 재고 현황 조회
    List<Map<String, Object>> getMaterialWarehouseStock(@Param("materialId") String materialId);
    
    // ==================== Material 테이블 관련 (부품/반제품) ====================
    
    // 자재 목록 조회 (검색 조건 포함)
    List<MaterialDTO> selectMaterialListFromMaterial(@Param("materialType") String materialType,
                                                     @Param("searchKeyword") String searchKeyword);
    
    // 공통코드 단위 목록 조회
    List<Map<String, Object>> getUnitList();
    
    // 자재 ID 중복 확인
    boolean existsMaterialById(@Param("materialId") String materialId);
    
    // 신규 자재 등록
    int insertIntoMaterial(MaterialDTO dto);
    
    // 자재 정보 수정
    int updateMaterialTable(MaterialDTO dto);
    
    // 자재 삭제 (다중 선택)
    int deleteMaterialsFromTable(@Param("list") List<String> materialIds);
    
    // 자재 최근 거래 내역 확인 (1개월 내)
    int checkRecentTransactionForMaterial(@Param("materialId") String materialId);
    
    // 자재 삭제 시 warehouse_item 함께 삭제
    int deleteWarehouseItemsByMaterial(@Param("materialId") String materialId);
    
    // ==================== Product 테이블 관련 (완제품) ====================
    
    // 제품 목록 조회 (검색 조건 포함)
    List<ProductDTO> selectProductList(@Param("productType") String productType,
                                       @Param("searchKeyword") String searchKeyword);
    
    // 신규 제품 등록
    int insertProduct(ProductDTO dto);
    
    // 제품 정보 수정
    int updateProduct(ProductDTO dto);
    
    // 제품 삭제 (다중 선택)
    int deleteProducts(@Param("list") List<String> productIds);
    
    // 제품 최근 거래 내역 확인 (1개월 내)
    int checkRecentTransaction(@Param("productId") String productId);
    
    // 제품 삭제 시 warehouse_item 함께 삭제
    int deleteWarehouseItemsByProduct(@Param("productId") String productId);
    
    // ==================== 창고 재고 관리 ====================
    
    // 특정 제품의 창고별 재고 현황 조회
    List<Map<String, Object>> getWarehouseStockByProduct(@Param("productId") String productId);
    
    // 창고-제품별 재고 수량 조회
    int getWarehouseItemQty(@Param("productId") String productId, 
                           @Param("warehouseId") String warehouseId);
    
    // 창고-제품별 재고 수량 업데이트
    int updateWarehouseItem(@Param("productId") String productId, 
                           @Param("warehouseId") String warehouseId,
                           @Param("newQty") Integer newQty);
    
    // 제품의 전체 재고 합계 계산
    Integer getTotalStockByProduct(@Param("productId") String productId);
    
    // 제품 테이블의 전체 수량 업데이트
    int updateProductQuantity(@Param("productId") String productId, 
                             @Param("totalQty") Integer totalQty);
    
    // ==================== 창고 위치 관리 ====================
    
    // 특정 타입의 운영중인 창고 목록 조회
    List<String> getActiveWarehousesByType(@Param("warehouseType") String warehouseType);
    
    // 창고 내 빈 위치 조회
    List<String> getEmptyLocations(@Param("warehouseId") String warehouseId);
    
    // warehouse_item 신규 등록 (위치 자동 배정)
    int insertWarehouseItem(@Param("productId") String productId,
                           @Param("warehouseId") String warehouseId,
                           @Param("initialQty") Integer initialQty,
                           @Param("empId") String empId);
    
    // warehouse_item 신규 등록 (위치 지정)
    int insertWarehouseItemWithLocation(@Param("productId") String productId,
                                        @Param("warehouseId") String warehouseId,
                                        @Param("locationId") String locationId,
                                        @Param("qty") Integer qty,
                                        @Param("empId") String empId);
    
    // 500개 미만 여유공간 있는 위치 조회
    List<Map<String, Object>> getProductLocationsWithSpace(@Param("productId") String productId, 
                                                           @Param("warehouseId") String warehouseId);
    
    // 제품 위치별 재고 조회 (수량 오름차순)
    List<Map<String, Object>> getProductLocationsByQty(@Param("productId") String productId, 
                                                       @Param("warehouseId") String warehouseId);
    
    // 특정 위치의 재고 수량 업데이트
    int updateLocationStock(@Param("productId") String productId, 
                           @Param("warehouseId") String warehouseId,
                           @Param("locationId") String locationId, 
                           @Param("newQty") Integer newQty);
    
    // 재고가 0인 위치 삭제
    int deleteEmptyLocation(@Param("productId") String productId, 
                           @Param("warehouseId") String warehouseId, 
                           @Param("locationId") String locationId);
    
    // ==================== Material 위치 관리 ====================
    
    // Material 기본 정보 조회
    MaterialDTO selectMaterialById(@Param("materialId") String materialId);
    
    // Material warehouse_item에 등록
    int insertMaterialStock(@Param("materialId") String materialId,
                           @Param("warehouseId") String warehouseId,
                           @Param("locationId") String locationId,
                           @Param("qty") Integer qty,
                           @Param("empId") String empId);

    // Material 위치별 재고 조회 (수량 오름차순)
    List<Map<String, Object>> getMaterialLocationsByQty(@Param("materialId") String materialId, 
                                                        @Param("warehouseId") String warehouseId);

    // Material 특정 위치 재고 업데이트
    int updateMaterialLocationStock(@Param("materialId") String materialId, 
                                   @Param("warehouseId") String warehouseId,
                                   @Param("locationId") String locationId, 
                                   @Param("newQty") Integer newQty);

    // Material 재고 0인 위치 삭제
    int deleteEmptyMaterialLocation(@Param("materialId") String materialId, 
                                   @Param("warehouseId") String warehouseId, 
                                   @Param("locationId") String locationId);
    
    // Material 특정 위치의 재고 수량 조회
    int getWarehouseItemQtyByLocation(@Param("materialId") String materialId, 
                                     @Param("warehouseId") String warehouseId,
                                     @Param("locationId") String locationId);
    
    // Material 창고별 재고 상세 조회 (위치 정보 포함)
    List<Map<String, Object>> getMaterialWarehouseStockDetail(@Param("materialId") String materialId);
    
    // ==================== 공통 ====================
    
    // 직원 이름 조회
    String selectEmployeeName(@Param("empId") String empId);
    
    // 전체 직원 목록 조회
    List<Map<String, String>> selectEmployeeList();
    
    // 자재 타입 공통코드 조회
    List<Map<String, String>> getMaterialTypes();
    
    // 품질검사기준 등록시 참조 - 품질관리
	List<MaterialDTO> findAllMaterials();
    
	// 검사방법 목록 조회
    List<Map<String, Object>> getInspectionMethods();

}