package com.bootstrap.study.approval.constant;

import org.modelmapper.ModelMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprDecision {
	//enum 값은 대문자로 지정
	
	REQUESTED("R"), // 기안 신청상태
    ACCEPT("A"), // 승인
    DENY("D");   // 반려
	
	private final String label;
	
	public String getCode() {
		return this.name();
	}
	
}
