package com.erp_mes.mes.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvDTO {
    private String manageId;        // 재고번호
    private String productId;       // 품목 코드
    private String productName;     // 품목 이름
    private String productType;     // 품목 종류
    private Integer itemAmount;     // 수량
    private String warehouseName;   // 창고명
}