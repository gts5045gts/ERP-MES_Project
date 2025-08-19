package com.bootstrap.study.approval.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprHalfType {
	//시작일, 종료일, 오전 오후인지 구분
	STARTMORNING("오전 반차"),
    STARTAFTERNOON("오후 반차"),
	ENDMORNING("오전 반차"),
    ENDAFTERNOON("오후 반차");

	private final String label;

	public String getCode() {
		return this.name();
	}

}
