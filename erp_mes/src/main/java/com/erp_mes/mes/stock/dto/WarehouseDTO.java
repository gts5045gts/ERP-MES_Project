package com.erp_mes.mes.stock.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//창고 정보 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDTO {
    private String warehouseId;
    private String warehouseName;
    private String warehouseType;
    private String warehouseStatus;
    private String warehouseLocation;
    private String empId;
    private String empName;  
    private String description;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}