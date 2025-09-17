package com.erp_mes.mes.stock.mapper;

import java.util.List;
import java.util.Map;

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
    
    // 0917 입고 관련 메서드 추가
    List<Map<String, Object>> selectInputList(@Param("inType") String inType, 
                                              @Param("inStatus") String inStatus);
    
    Integer getTodayInputCount(@Param("today") String today);
    
    int insertInput(Map<String, Object> params);
    
    Map<String, Object> selectInputById(@Param("inId") String inId);
    
    int updateInputStatus(@Param("inId") String inId, @Param("status") String status);
    
    int increaseStock(@Param("warehouseId") String warehouseId,
                      @Param("productId") String productId,
                      @Param("locationId") String locationId,
                      @Param("inCount") Integer inCount);
    
    List<Map<String, Object>> selectPartsList();
    
    List<Map<String, Object>> selectClientsList();
    
    List<WarehouseDTO> selectWarehouseListByType(@Param("warehouseType") String warehouseType);
    
    List<String> getEmptyLocations(@Param("warehouseId") String warehouseId); 
}