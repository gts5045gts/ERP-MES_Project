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
public class OutputDTO {
	private String productId;
	private String orderId;
	private String outType;
	private Integer outCount;
	private LocalDateTime updateDate;
}
