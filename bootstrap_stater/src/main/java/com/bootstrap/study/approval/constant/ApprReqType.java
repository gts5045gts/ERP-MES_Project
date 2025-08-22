package com.bootstrap.study.approval.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprReqType {

	//순서 기본을 첫번째로 변경해야함
	VACATION("휴가신청서"),
	BASIC("기본"),
	SPENDING("지출결의서");
	
	private final String label;
	
	public String getCode()	{
		return this.name();
	}
	
}
