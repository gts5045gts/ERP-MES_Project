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
public class WorkOrderDTO {
	
	private Long workOrderId;
	private String planId;
	private String bomId;
	private String lineId;
	private String empId;
	private String workOrderStatus;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate createdAt;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate updatedAt;
	
	// 추가로 보여줄것
	private String empName;
	private String productName;
	private String materialName;
	private Integer planQuantity;
	
    private String lotId; 

    // JOIN을 통해 가져올 필드
    private String productId;
    private String processName;
    private String inspectionType;
    private String inspectionTypeName;
    
}
