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

	private String product_id;
	private String product_name;
	private String product_type;
	private String unit;
	private LocalDateTime created_at;
	private LocalDateTime updated_at;
	private String spec;
}
