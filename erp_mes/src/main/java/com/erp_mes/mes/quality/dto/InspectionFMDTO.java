package com.erp_mes.mes.quality.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class InspectionFMDTO {
    private Long inspectionFMId;
    private String inspectionType;
    private String itemName;
    private String methodName;
}