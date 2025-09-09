package com.erp_mes.erp.attendance.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CommuteDeleteLogDTO {
	private Long logId;
	private String empId; // 삭제를한 관리자ID
	private String targetEmpId; // 삭제된 사원ID
	private LocalDateTime checkInTime; // 삭제된 출근시간
	private String commuteDate; // 삭제된 출근날짜
	private LocalDateTime deletedAt; // 삭제 시각
	private String deleteReason; // 삭제 사유
	// ---------------------------------------------
	private String targetEmpNm; // 삭제된 사원의 이름
	private String targetEmpDept; // 삭제된 사원의 부서
	private String targetEmpPos; // 삭제된 사원의 직책
	// ---------------------------------------------
	private String adminNm; // 관리자 이름
	private String adminDept; // 관리자 부서
	private String adminPos; // 관리자 직책
	
	
}
