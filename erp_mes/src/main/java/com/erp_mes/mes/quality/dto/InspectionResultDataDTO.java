package com.erp_mes.mes.quality.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InspectionResultDataDTO {
    private String itemId;
    private Float measurement; // 실측값
    private String result; // 합격/불합격 (Y/N)
    private String remarks; // 비고
}