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
	private String empId;
	private String deleteEmpId;
	private LocalDateTime checkInTime;
	private LocalDateTime deletedAt;
	private String DelteReason;

}
