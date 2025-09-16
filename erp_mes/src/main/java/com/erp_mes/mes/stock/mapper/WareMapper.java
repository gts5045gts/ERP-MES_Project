package com.erp_mes.mes.stock.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.erp_mes.mes.stock.dto.WarehouseDTO;

@Mapper
public interface WareMapper {
    
    // 창고 목록 조회
    List<WarehouseDTO> selectWarehouseList(@Param("warehouseType") String warehouseType,
                                           @Param("warehouseStatus") String warehouseStatus,
                                           @Param("searchKeyword") String searchKeyword);
    
    // 창고 ID 중복 체크
    boolean existsWarehouseById(@Param("warehouseId") String warehouseId);
    
    // 창고 등록
    int insertWarehouse(WarehouseDTO dto);
    
    // 창고 수정
    int updateWarehouse(WarehouseDTO dto);
    
    // 창고 삭제
    int deleteWarehouses(@Param("list") List<String> warehouseIds);
    
    // 창고 사용중 체크
    int checkWarehouseInUse(@Param("warehouseId") String warehouseId);
}