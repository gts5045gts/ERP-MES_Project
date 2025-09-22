package com.erp_mes.mes.business.dto;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ShipmentDTO {
	private String shipmentId;
	private String orderId;
	private String clientId;
	private String empId;
	private String shipmentStatus;
	private Date shipmentDate;
	private Date deliveryDate;
	private LocalDateTime updateAt;
	
	private String clientName; // 조회용
	private String empName; // 조회용
	
	private List<ShipmentDetailDTO> items;
}
