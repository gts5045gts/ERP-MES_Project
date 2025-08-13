package com.bootstrap.study.approval.dto;

import java.time.LocalDateTime;

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
	private	String	decision; //승인 반려 상태
	private	LocalDateTime decDate;
	private	String	comments;
	
	private static ModelMapper modelMapper = new ModelMapper();
	
//	public ApprLine toEntity() {
//		return modelMapper.map(this, Appr)
//	}

}
