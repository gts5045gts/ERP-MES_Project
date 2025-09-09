package com.erp_mes.erp.approval.constant;

import lombok.Getter; 
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprHalfType {
	//시작일, 종료일, 오전 오후인지 구분
	STARTMORNING("시작일 오전"),
    STARTAFTERNOON("시작일 오후"),
	ENDMORNING("종료일 오전"),
    ENDAFTERNOON("종료일 오후");

	private final String label;

	public String getCode() {
		return this.name();
	}

}
