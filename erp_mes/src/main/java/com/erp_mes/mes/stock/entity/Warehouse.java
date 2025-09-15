package com.erp_mes.mes.stock.entity;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    private String warehouseId;
    private String warehouseName;
    private String warehouseType;
    private String warehouseStatus;
    private String warehouseLocation;
    private String empId;
    private String description;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}