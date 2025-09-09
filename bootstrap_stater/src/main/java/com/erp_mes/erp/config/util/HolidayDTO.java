package com.erp_mes.erp.config.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class HolidayDTO {
	private String dateKind;  // "01" (법정공휴일), "02" (기념일) 등
    private String dateName;  // 공휴일 이름 (예: "신정")
    private String isHoliday; // "Y" 또는 "N"
    private String locdate;   // 날짜 (예: "20250101")
}
