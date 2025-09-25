package com.erp_mes.mes.pm.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrdersDetailDTO {
	
	private Long id;
	private String orderId;
	private String productId;
	private String productName;
	private Long orderQty;
	private String orderDetailStatus;
	private LocalDate deliveryDate;
    
} 
