package com.erp_mes.mes.quality.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionResult {
    private Long resultId;
    private Long inspectionId;
    private Long itemId;
    private String result;
    private String remarks;
}