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
public class MaterialDTO {
    private String materialId;
    private String materialName;
    private String materialType;     
    private String materialTypeName; 
    private String unit;
    private String spec;
    private Double price;
    private Integer quantity;
    private String empId;
    private String empName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
