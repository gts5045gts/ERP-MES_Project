package com.erp_mes.mes.business.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ShipmentDetailDTO {
	private Integer id;
	private String shipmentId;
	private String productId;
	private Integer orderQty;
	private Integer shipmentQty;
	private String shipmentDetailStatus;
	private LocalDateTime createAt;
	private LocalDateTime updateAt;
	private String orderId;
	private String clientId;
	
	private String productName;
	private Integer stockQty; 	// warehouse_item 재고량
}
