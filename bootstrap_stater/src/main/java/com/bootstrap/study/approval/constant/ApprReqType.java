package com.bootstrap.study.approval.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprReqType {

	BASIC("기본"),
	VACATION("휴가신청서"),
	SPENDING("지출결의서");
	
	private final String label;
	
	public String getCode()	{
		return this.name();
	}
	
}
