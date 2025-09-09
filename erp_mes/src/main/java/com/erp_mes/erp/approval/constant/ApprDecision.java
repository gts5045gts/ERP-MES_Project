package com.erp_mes.erp.approval.constant;

import lombok.Getter; 
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprDecision {
	//enum 값은 대문자로 지정	
    ACCEPT("승인"), 
    PENDING("대기"),
    DENY("반려"); 
	
	private final String label;
	
	public String getCode() {
		return this.name();
	}	
}
