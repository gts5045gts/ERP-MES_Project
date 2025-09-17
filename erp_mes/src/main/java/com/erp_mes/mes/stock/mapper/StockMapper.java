package com.erp_mes.mes.stock.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.mes.stock.dto.ProductDTO;
import com.erp_mes.mes.stock.dto.StockDTO;
import com.erp_mes.mes.stock.dto.WarehouseDTO;

@Mapper
public interface StockMapper {
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
    
    List<ProductDTO> selectMaterialList(@Param("productType") String productType,
            @Param("searchKeyword") String searchKeyword);
    
    boolean existsMaterialById(@Param("productId") String productId);

    int insertMaterial(ProductDTO dto);

    int updateMaterial(ProductDTO dto);

    int deleteMaterials(@Param("list") List<String> productIds);
    
    String selectEmployeeName(@Param("empId") String empId);
    
    int checkRecentTransaction(@Param("productId") String productId);
    
    // 완/반제품 목록 관리
    List<ProductDTO> selectProductList(@Param("productType") String productType,
            @Param("searchKeyword") String searchKeyword);

	int insertProduct(ProductDTO dto);
	
	int updateProduct(ProductDTO dto);
	
	int deleteProducts(@Param("list") List<String> productIds);

	List<Map<String, String>> selectEmployeeList();
	
	// 0916 특정 제품의 창고별 재고 조회
	List<Map<String, Object>> getWarehouseStockByProduct(@Param("productId") String productId);
	
	// 창고별 재고 조정 관련 메소드들
	int getWarehouseItemQty(@Param("productId") String productId, 
	                        @Param("warehouseId") String warehouseId);

	int updateWarehouseItem(@Param("productId") String productId, 
	                        @Param("warehouseId") String warehouseId,
	                        @Param("newQty") Integer newQty);

	Integer getTotalStockByProduct(@Param("productId") String productId);

	int updateProductQuantity(@Param("productId") String productId, 
							  @Param("totalQty") Integer totalQty);
	
	// 운영중인 특정 타입 창고 조회
	List<String> getActiveWarehousesByType(@Param("warehouseType") String warehouseType);

	// warehouse_item 등록
	int insertWarehouseItem(@Param("productId") String productId,
            				@Param("warehouseId") String warehouseId,
            				@Param("initialQty") Integer initialQty,
            				@Param("empId") String empId);
	
	// 빈 위치 조회
	List<String> getEmptyLocations(@Param("warehouseId") String warehouseId);

	// 위치 지정해서 warehouse_item 등록
	int insertWarehouseItemWithLocation(@Param("productId") String productId,
	                                    @Param("warehouseId") String warehouseId,
	                                    @Param("locationId") String locationId,
	                                    @Param("qty") Integer qty,
	                                    @Param("empId") String empId);
	
	List<Map<String, Object>> getProductLocationsWithSpace(@Param("productId") String productId, @Param("warehouseId") String warehouseId);
	
	List<Map<String, Object>> getProductLocationsByQty(@Param("productId") String productId, @Param("warehouseId") String warehouseId);
	
	int updateLocationStock(@Param("productId") String productId, @Param("warehouseId") String warehouseId,
							@Param("locationId") String locationId, @Param("newQty") Integer newQty);
	
	int deleteEmptyLocation(@Param("productId") String productId, @Param("warehouseId") String warehouseId, @Param("locationId") String locationId);
	
	int deleteWarehouseItemsByProduct(@Param("productId") String productId);
	
}