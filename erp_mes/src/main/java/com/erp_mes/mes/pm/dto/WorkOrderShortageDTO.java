package com.erp_mes.mes.pm.dto;

import java.sql.Date;
import java.time.LocalDate;

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
	
	private String materialName; 	// 조회용
    private Integer materialCount;	// 작업지시당 부족한 자재 종류의 수
    private Integer price;
    private String unit;
    private Integer totalQty;		// 작업지시에 부족한 총 자재량
    private Integer totalPrice;			// 발주 자재 목록에서 가격(변동될 수도 있음!)
    private Date startDate;
   
}
