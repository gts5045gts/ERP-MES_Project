package com.erp_mes.erp.approval.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprReqType {

	BASIC("기본"),
	VACATION("휴가신청서"),
	SPENDING("지출결의서"),
	TRANSFER("인사발령");
	
	private final String label;
	
	public String getCode()	{
		return this.name();
	}
	
	// 이름으로 enum 찾기
    public static ApprReqType fromName(String name) {
        for (ApprReqType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant for name: " + name);
    }
	
}
