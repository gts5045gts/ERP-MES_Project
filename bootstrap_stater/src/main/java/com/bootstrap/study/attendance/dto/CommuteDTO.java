package com.bootstrap.study.attendance.dto;


import java.sql.Timestamp;
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
public class CommuteDTO {
	private Long commuteId;
	private LocalDateTime checkInTime;
	private LocalDateTime checkOutTime;
	private String empId;
	private String workStatus;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	
	
	@Builder
	public CommuteDTO(Long commuteId, LocalDateTime checkInTime, LocalDateTime checkOutTime, String empId,
			String workStatus, Timestamp createdAt, Timestamp updatedAt) {
		this.commuteId = commuteId;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
		this.empId = empId;
		this.workStatus = workStatus;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
}










