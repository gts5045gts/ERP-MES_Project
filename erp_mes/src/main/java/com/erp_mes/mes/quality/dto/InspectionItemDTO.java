package com.erp_mes.mes.quality.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionItemDTO {
    private Long itemId;
    private String inspectionType;
    private String itemName;
    private Double toleranceValue;
    private String unit;
}