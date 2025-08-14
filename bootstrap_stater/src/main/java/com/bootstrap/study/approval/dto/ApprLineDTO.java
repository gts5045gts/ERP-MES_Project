package com.bootstrap.study.approval.dto;

import java.time.LocalDateTime;

import com.bootstrap.study.approval.constant.ApprDecision;
import com.bootstrap.study.approval.entity.AppLine;
import com.bootstrap.study.approval.entity.Appr;
import org.modelmapper.ModelMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApprLineDTO {
	
	private	Long lineId;
	private	Integer	reqId; //결제문서 id
	private	Integer	stepNo; // 결제 순번 
	private	String	apprId; //결제자 id
	private ApprDecision decision = ApprDecision.REQUESTED; //승인 반려 , 현재 기안신청중
	private	LocalDateTime decDate;
	private	String	comments;
	
	private static ModelMapper modelMapper = new ModelMapper();

	public AppLine toEnity() { return modelMapper.map(this, AppLine.class); }
	
	public static ApprLineDTO fromEntity(AppLine appLine) { return modelMapper.map(appLine, ApprLineDTO.class); }
}
