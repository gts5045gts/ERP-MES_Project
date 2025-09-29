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
public class DefectDTO {
	
	private Long defectItemId;					// 불량 ID
	private Long workOrderId;					// 불량 ID
	private String defectType;					// 불량 유형
	private String defectReason;				// 불량 사유
	private Long defectQty;						// 불량 수량
	private String productNm;					// 불량 제품명
	private String empId;						// 작업자 ID
	private Long defectLocation;				// 불량위치
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime defectDate;			// 불량 발생일자

	private String lotId;

	private Long resultId; // 실적 id
	
	// =================================================
	// 불량관리에서 사용
	private String empName;
	private String defectTypeName;
	private String defectLocationName;
	

}
