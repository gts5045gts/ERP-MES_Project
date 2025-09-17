package com.erp_mes.mes.pm.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductDTO {

	private String productId;
	private String productName;
	private String productType;
	private String unit;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String spec;
}
