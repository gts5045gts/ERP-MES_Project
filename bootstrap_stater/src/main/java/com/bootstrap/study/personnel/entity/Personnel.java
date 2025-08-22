package com.bootstrap.study.personnel.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.UpdateTimestamp;

import com.bootstrap.study.personnel.dto.PersonnelDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "employee")
public class Personnel {

	// 사원번호
	@Id
	@Column(nullable = false, name = "emp_id")
	private String empId;

	// 이름
	@Column(nullable = false, name = "emp_name")
	private String name;

	// 사원 비밀번호
	@Column(nullable = false, unique = true, name = "emp_passwd")
	private String passwd;

	// 주민번호
	@Column(nullable = false, name = "emp_resid")
	private String resident;

	// 우편번호
	@Column(nullable = false, name = "emp_addr_num")
	private String addrNum;

	// 주소
	@Column(nullable = false, name = "emp_addr1")
	private String addr1;

	// 상세주소
	@Column(nullable = false, name = "emp_addr2")
	private String addr2;

	// 이메일
	@Column(nullable = false, name = "emp_email")
	private String email;

	// 전화번호
	@Column(nullable = false, name = "emp_phone")
	private String phone;

	// 입사일
	@Column(nullable = false, name = "emp_join_date")
	private String joinDate;

	// 퇴사일
	@Column(nullable = false, name = "emp_resign_date")
	private String resignDate;

	// 수정일
	@UpdateTimestamp
	@Column(nullable = false, name = "update_at")
	private Timestamp update;

	// 재직상태
	@Column(nullable = false, name = "emp_status")
	private String status;

	// 부서
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "emp_dept_id", referencedColumnName = "dept_id")
	private Department department;

	// 직급
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "emp_position", referencedColumnName = "pos_id")
	private Position position; // 직책
	
	public static Personnel fromDTO(PersonnelDTO peronnelDTO) {
		
		Department department = new Department();
		department.setDeptId(peronnelDTO.getDeptId());
		department.setDeptName(peronnelDTO.getDeptName());
		Position position = new Position();
		position.setPosId(peronnelDTO.getPosId());
		position.setPosName(peronnelDTO.getPosName());
		
		Personnel personnel = new Personnel();
		personnel.setEmpId(peronnelDTO.getEmpId());
		personnel.setName(peronnelDTO.getName());
		personnel.setPasswd(peronnelDTO.getPasswd());
		personnel.setResident(peronnelDTO.getResident());
		personnel.setAddrNum(peronnelDTO.getAddrNum());
		personnel.setAddr1(peronnelDTO.getAddr1());
		personnel.setAddr2(peronnelDTO.getAddr2());
		personnel.setEmail(peronnelDTO.getEmail());
		personnel.setPhone(peronnelDTO.getPhone());
		personnel.setJoinDate(peronnelDTO.getJoinDate());
		personnel.setResignDate(peronnelDTO.getResignDate());
		personnel.setUpdate(peronnelDTO.getUpdate());
		personnel.setStatus(peronnelDTO.getStatus());
		personnel.setName(peronnelDTO.getName());
		personnel.setName(peronnelDTO.getName());
		personnel.setDepartment(department);
		personnel.setPosition(position);
		
		
		return personnel;
	}

}