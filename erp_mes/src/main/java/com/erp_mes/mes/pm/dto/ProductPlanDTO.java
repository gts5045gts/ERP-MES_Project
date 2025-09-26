package com.erp_mes.mes.pm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductPlanDTO {
	private String planId;
	private String productId;
	private String bomId;
	private String productName;
	private String planStatus;
	private String orderId;
	private Integer planQuantity;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dueDate;
	private String empId;
	private String empName;
	
	// 추가 컬럼
	private String routeGroupName;
    private String materialId;
    private String materialName;
    private BigDecimal bomQuantity;
    private BigDecimal totalNeededQuantity;
    private BigDecimal stockQuantity;
    private String workOrderPossible;
}
