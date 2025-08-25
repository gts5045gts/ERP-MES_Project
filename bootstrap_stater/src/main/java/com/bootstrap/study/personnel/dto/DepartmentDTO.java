package com.bootstrap.study.personnel.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DepartmentDTO {
	private Long id; // 부서 ID (dept_id)
	private String name; // 부서명 (dept_name)
}
