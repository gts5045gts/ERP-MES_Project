package com.erp_mes.mes.quality.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionItemDTO {
    private Long itemId;
    private String productId; // 제품번호
    private Long inspectionFMId; // INSPECTION_FM_ID 검사기준값
    private BigDecimal toleranceValue; // 허용 공차
    private String unit;
    
    // FM테이블에서 참조
    private String inspectionType;
    private String itemName;
    private String methodName;
}