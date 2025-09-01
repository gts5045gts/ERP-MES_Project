package com.bootstrap.study.attendance.dto;

import java.time.LocalDateTime;

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
	private String empId; // 삭제한 관리자ID
	private String deleteEmpId; // 삭제된 사원ID
	private LocalDateTime checkInTime; // 삭제된 출근시간
	private String commuteDate; // 삭제된 출근날짜
	private LocalDateTime deletedAt; // 삭제 시각
	private String deleteReason; // 삭제 사유
}
