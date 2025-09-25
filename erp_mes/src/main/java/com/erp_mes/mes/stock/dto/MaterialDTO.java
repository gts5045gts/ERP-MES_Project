package com.erp_mes.mes.stock.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//자재(부품/반제품) 정보 DTO
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
    private Integer inspectionFmId;
    private String inspectionItemName;
    private Integer quantity;
    private String empId;
    private String empName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

