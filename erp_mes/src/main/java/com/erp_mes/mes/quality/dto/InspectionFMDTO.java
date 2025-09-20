package com.erp_mes.mes.quality.dto;

import com.erp_mes.mes.quality.entity.InspectionFM;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InspectionFMDTO {
    private Long inspectionFMId;
    private String inspectionType;
    private String inspectionTypeName;
    private String itemName;
    private String methodName;
    
    // DTO -> Entity 변환 메서드
    public InspectionFM toEntity() {
        return InspectionFM.builder()
        		.inspectionFMId(this.inspectionFMId)
                .inspectionType(this.inspectionType)
                .itemName(this.itemName)
                .methodName(this.methodName)
                .build();
    }
}