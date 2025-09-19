package com.erp_mes.mes.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//재고 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private String productId;        	
    private String productName;      	
    private String productType;      	
    private Integer itemAmount; 
    private Integer quantity;
    private String warehouseName;    	
    private String warehouseId;      	
    private String locationId;       	
    private String warehouseLocation;	
}