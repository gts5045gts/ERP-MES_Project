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
    
    // 재고 목록 조회
    List<StockDTO> getStockList(@Param("productName") String productName, 
                                @Param("warehouseId") String warehouseId);
    
    // 창고 목록 조회
    List<WarehouseDTO> getWarehouseList();
    
    // 재고 상세 조회
    StockDTO getStockDetail(@Param("productId") String productId);
    
    // 재고 수량 업데이트
    int updateStockAmount(@Param("productId") String productId, 
                         @Param("warehouseId") String warehouseId,
                         @Param("itemAmount") Integer itemAmount);
    
    // ==================== Material 테이블 관련 (부품/반제품) ====================
    
    // 자재 목록 조회
    List<MaterialDTO> selectMaterialListFromMaterial(@Param("materialType") String materialType,
                                                     @Param("searchKeyword") String searchKeyword);
    
    // 자재 ID 중복 체크
    boolean existsMaterialById(@Param("materialId") String materialId);
    
    // 자재 등록
    int insertIntoMaterial(MaterialDTO dto);
    
    // 자재 수정
    int updateMaterialTable(MaterialDTO dto);
    
    // 자재 삭제
    int deleteMaterialsFromTable(@Param("list") List<String> materialIds);
    
    // 자재 최근 거래 체크
    int checkRecentTransactionForMaterial(@Param("materialId") String materialId);
    
    // 자재별 warehouse_item 삭제
    int deleteWarehouseItemsByMaterial(@Param("materialId") String materialId);
    
    // ==================== Product 테이블 관련 (완제품) ====================
    
    // 제품 목록 조회
    List<ProductDTO> selectProductList(@Param("productType") String productType,
                                       @Param("searchKeyword") String searchKeyword);
    
    // 제품 등록
    int insertProduct(ProductDTO dto);
    
    // 제품 수정
    int updateProduct(ProductDTO dto);
    
    // 제품 삭제
    int deleteProducts(@Param("list") List<String> productIds);
    
    // 제품 최근 거래 체크
    int checkRecentTransaction(@Param("productId") String productId);
    
    // 제품별 warehouse_item 삭제
    int deleteWarehouseItemsByProduct(@Param("productId") String productId);
    
    // ==================== 창고 재고 관리 ====================
    
    // 특정 제품의 창고별 재고 조회
    List<Map<String, Object>> getWarehouseStockByProduct(@Param("productId") String productId);
    
    // 창고별 재고 수량 조회
    int getWarehouseItemQty(@Param("productId") String productId, 
                           @Param("warehouseId") String warehouseId);
    
    // 창고별 재고 업데이트
    int updateWarehouseItem(@Param("productId") String productId, 
                           @Param("warehouseId") String warehouseId,
                           @Param("newQty") Integer newQty);
    
    // 제품 전체 재고 계산
    Integer getTotalStockByProduct(@Param("productId") String productId);
    
    // 제품 수량 업데이트
    int updateProductQuantity(@Param("productId") String productId, 
                             @Param("totalQty") Integer totalQty);
    
    // ==================== 창고 위치 관리 ====================
    
    // 운영중인 특정 타입 창고 조회
    List<String> getActiveWarehousesByType(@Param("warehouseType") String warehouseType);
    
    // 빈 위치 조회
    List<String> getEmptyLocations(@Param("warehouseId") String warehouseId);
    
    // warehouse_item 등록 (위치 없이)
    int insertWarehouseItem(@Param("productId") String productId,
                           @Param("warehouseId") String warehouseId,
                           @Param("initialQty") Integer initialQty,
                           @Param("empId") String empId);
    
    // warehouse_item 등록 (위치 지정)
    int insertWarehouseItemWithLocation(@Param("productId") String productId,
                                        @Param("warehouseId") String warehouseId,
                                        @Param("locationId") String locationId,
                                        @Param("qty") Integer qty,
                                        @Param("empId") String empId);
    
    // 여유 공간 있는 위치 조회
    List<Map<String, Object>> getProductLocationsWithSpace(@Param("productId") String productId, 
                                                           @Param("warehouseId") String warehouseId);
    
    // 제품 위치별 수량순 조회 (적은 것부터)
    List<Map<String, Object>> getProductLocationsByQty(@Param("productId") String productId, 
                                                       @Param("warehouseId") String warehouseId);
    
    // 위치별 재고 업데이트
    int updateLocationStock(@Param("productId") String productId, 
                           @Param("warehouseId") String warehouseId,
                           @Param("locationId") String locationId, 
                           @Param("newQty") Integer newQty);
    
    // 빈 위치 삭제
    int deleteEmptyLocation(@Param("productId") String productId, 
                           @Param("warehouseId") String warehouseId, 
                           @Param("locationId") String locationId);
    
    // ==================== 공통 ====================
    
    // 직원 이름 조회
    String selectEmployeeName(@Param("empId") String empId);
    
    // 직원 목록 조회
    List<Map<String, String>> selectEmployeeList();
    
    List<Map<String, String>> getMaterialTypes();
}