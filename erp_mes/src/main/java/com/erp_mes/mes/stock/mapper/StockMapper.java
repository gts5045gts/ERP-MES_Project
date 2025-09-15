package com.erp_mes.mes.stock.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}
