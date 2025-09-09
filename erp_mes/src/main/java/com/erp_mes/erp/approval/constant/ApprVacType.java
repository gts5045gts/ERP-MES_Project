package com.erp_mes.erp.approval.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprVacType {
	
	LEAVE("연차"),
	HALF_LEAVE("반차");
	
	private final String label;
	
	public String getCode()	{
		return this.name();
	}
	
}
