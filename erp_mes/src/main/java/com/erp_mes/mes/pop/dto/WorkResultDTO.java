package com.erp_mes.mes.pop.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class WorkResultDTO {
	
	private Long resultId;		// 실적 id
	private Long workOrderId; 		// 작업지시 id (fk)
	private Long goodQty;			// 생산수량
	private Long defectItemId;		// 항목 ID(불량) (fk)
	private Long defectQty;			// 불량수량
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt; // 등록시간
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt; // 수정시간
	
	// ========================================
	
	private Long bomId;
	private String processId;
    private String equipmentId;
    private String empId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String workOrderStatus;
	

}
