package com.bootstrap.study.personnel.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "employee")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Personnel {

	@Id
	@Column(nullable = false, unique = true, name = "emp_id")
	private String empId; // 사원번호
	
	@Column(nullable = false, unique = true, name = "emp_cd")
	private Long empCd; // 사원코드값
	
	@Column(nullable = false, unique = true, name = "emp_passwd")
	private String passwd; // 사원 비밀번호

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "emp_dept_id", referencedColumnName = "dept_id")
	private Department department; // 부서명

	@Column(nullable = false, name = "emp_position")
	private Long position; // 직책

	@Column(nullable = false, name = "emp_name")
	private String name; // 이름.

	@Column(nullable = false, name = "emp_phone")
	private String phone; // 전화번호

	@Column(nullable = false, name = "emp_email")
	private String email; // 이메일

}
