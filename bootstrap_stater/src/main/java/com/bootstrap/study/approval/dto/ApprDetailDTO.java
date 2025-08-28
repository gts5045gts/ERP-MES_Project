package com.bootstrap.study.approval.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;

import com.bootstrap.study.approval.constant.ApprHalfType;
import com.bootstrap.study.approval.constant.ApprVacType;
import com.bootstrap.study.approval.entity.ApprDetail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ApprDetailDTO {
	
	private	Long detId;
	private	Long reqId;
	private	LocalDate startDate;
	private	LocalDate endDate;
	private	ApprVacType vacType;
	private	ApprHalfType halfType;
	private	LocalDateTime complateDate;
	
	private static ModelMapper modelMapper = new ModelMapper();
	
	public ApprDetail toEntity () { return modelMapper.map(this, ApprDetail.class); }
	
	public static ApprDetailDTO fromEntity(ApprDetail apprDetail) { return modelMapper.map(apprDetail, ApprDetailDTO.class); }

}
