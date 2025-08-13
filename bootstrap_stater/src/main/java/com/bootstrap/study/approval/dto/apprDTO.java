package com.bootstrap.study.approval.dto;

import java.time.LocalDateTime;

import com.bootstrap.study.approval.constant.apprStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class apprDTO {
	
	private Long request_id;
	
	private String empId;
	
	private String reqType;
	
	@NotBlank(message = "제목은 필수 입력값입니다!") // 공백만 있거나, 길이가 0인 문자열, null 값을 허용하지 않음
	private String title;
	
	private String content;
	
	private LocalDateTime createDate;
	
	private LocalDateTime updateDate;
	
 	private apprStatus status = apprStatus.PROCESSING;
 	
 	private Integer currentStep;
 	
 	private Integer totStep;

 	@Builder
	public apprDTO(Long request_id, String empId, String reqType, String title, String content, LocalDateTime createDate,
			LocalDateTime updateDate, apprStatus status, Integer currentStep, Integer totStep) {
		this.request_id = request_id;
		this.empId = empId;
		this.reqType = reqType;
		this.title = title;
		this.content = content;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.status = status;
		this.currentStep = currentStep;
		this.totStep = totStep;
	}
 	
 	

}
