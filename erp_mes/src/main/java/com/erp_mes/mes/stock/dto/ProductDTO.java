package com.erp_mes.mes.stock.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String productId;
    private String productName;
    private String productType;
    private String unit;
    private Integer price;        
    private String empId;       
    private String empName;      
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
