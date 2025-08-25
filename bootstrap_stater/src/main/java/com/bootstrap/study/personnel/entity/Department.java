package com.bootstrap.study.personnel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "test_dept")
public class Department {
	@Id
	@Column(name = "dept_id")
	private Long deptId;

	@Column(name = "dept_name")
	private String deptName;
}
