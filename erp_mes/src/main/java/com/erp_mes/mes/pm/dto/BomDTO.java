package com.erp_mes.mes.pm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BomDTO {
	private Long bomId;
	private String productId;
	private String materialId;
	private String materialName;
	private BigDecimal quantity;
	private String unit;
	private Integer revisionNo;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
