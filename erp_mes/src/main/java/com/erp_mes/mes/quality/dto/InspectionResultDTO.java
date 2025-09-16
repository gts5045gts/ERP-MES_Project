package com.erp_mes.mes.quality.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionResultDTO {
    private Long resultId;
    private Long inspectionId;
    private Long itemId;
    private String result;
    private String remarks;
    private Double resultValue;
}