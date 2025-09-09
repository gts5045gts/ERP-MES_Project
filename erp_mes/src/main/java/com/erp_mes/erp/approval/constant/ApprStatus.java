package com.erp_mes.erp.approval.constant;

import lombok.Getter; 
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprStatus {
	//enum 값은 대문자로 지정
	
	REQUESTED("기안신청"), 	// 기안 신청
    PROCESSING("진행중"), 	// 진행중
    FINISHED("완료"),   	// 완료
	CANCELED("반려됨");		// 거절
	private final String label;
	
	public String getCode() {
		return this.name();
	}
	

}