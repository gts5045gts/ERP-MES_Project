package com.erp_mes.mes.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private String productId;        	// 품목코드
    private String productName;      	// 품목명
    private String productType;      	// 품목종류
    private Integer itemAmount;      	// 재고수량
    private String warehouseName;    	// 창고명
    private String warehouseId;      	// 창고ID
    private String locationId;       	// 위치ID
    private String warehouseLocation;	// 창고 위치
}
