package com.bootstrap.study.approval.dto;

import java.time.LocalDateTime;

import com.bootstrap.study.approval.constant.ApprStatus;

import com.bootstrap.study.approval.entity.Appr;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ApprDTO {
	
	private Long reqId;
	
	private String empId;
	
	private String reqType;
	
	@NotBlank(message = "제목은 필수 입력값입니다!") // 공백만 있거나, 길이가 0인 문자열, null 값을 허용하지 않음
	private String title;
	
	private String content;
	
	private LocalDateTime createAt;
	
	private LocalDateTime updateAt;
	
 	private ApprStatus status = ApprStatus.REQUESTED;
 	
 	private Integer currentStep;
 	
 	private Integer totStep;

 	@Builder
	public ApprDTO(Long reqId, String empId, String reqType, String title, String content, LocalDateTime createAt,
			LocalDateTime updateAt, ApprStatus status, Integer currentStep, Integer totStep) {
		this.reqId = reqId;
		this.empId = empId;
		this.reqType = reqType;
		this.title = title;
		this.content = content;
		this.createAt = createAt;
		this.updateAt = updateAt;
		this.status = status;
		this.currentStep = currentStep;
		this.totStep = totStep;
	}

	private static ModelMapper modelMapper = new ModelMapper();

 	public Appr toEntity() { return modelMapper.map(this, Appr.class); }

	public static ApprDTO fromEntity(Appr appr) { return modelMapper.map(appr, ApprDTO.class); }
}
