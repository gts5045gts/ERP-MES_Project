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
public class MaterialDTO {
	private String materialId;
	private BigDecimal quantity;
	private String unit;
}
