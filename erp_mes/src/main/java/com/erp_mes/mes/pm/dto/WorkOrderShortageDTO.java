package com.erp_mes.mes.pm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class WorkOrderShortageDTO {
	
	private Long shortId;
	private Long workOrderId;
	private String materialId;
	private Long requireQty;
	private String status;
	private LocalDate requestDate;
    
}
