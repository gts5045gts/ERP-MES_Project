package com.erp_mes.mes.stock.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseItem {
    private String manageId;
    private String warehouseId;
    private String productId;
    private Integer itemAmount;
    private Integer maxAmount;
    private String useYn;
    private String description;
    private String locationId;
    private String empId;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
