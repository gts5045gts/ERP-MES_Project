package com.bootstrap.study.approval.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprStatus {
	//enum 값은 대문자로 지정
	
	REQUESTED("R"), // 기안 신청
    PROCESSING("P"), // 진행중
    FINISHED("F");   // 완료
	
	private final String label;
	
	public String getCode() {
		return this.name();
	}
	

}
