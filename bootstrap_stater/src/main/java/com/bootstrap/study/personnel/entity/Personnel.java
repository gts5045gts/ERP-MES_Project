package com.bootstrap.study.personnel.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.UpdateTimestamp;

import com.bootstrap.study.commonCode.entity.CommonDetailCode;
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
import lombok.ToString;

@ToString
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
	@Column(name = "emp_resign_date")
	private String resignDate;

	// 수정일
	@UpdateTimestamp
	@Column(nullable = false, name = "update_at")
	private Timestamp update;

//	// 재직상태
//	@Column(nullable = false, name = "emp_status")
//	private String status;

	// 부서
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "emp_dept_id", referencedColumnName = "com_dt_id")
	private CommonDetailCode department;

	// 직급
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "emp_position", referencedColumnName = "com_dt_id")
	private CommonDetailCode position; // 직책

	
	//추가한 컬럼 보안등급---------------------------
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "emp_level_id", referencedColumnName = "com_dt_id")
	private CommonDetailCode level;
	
	
//	재직현황
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "emp_status", referencedColumnName = "com_dt_id")
	private CommonDetailCode status;
	
	
	
	public static Personnel fromDTO(PersonnelDTO peronnelDTO) {
		
		CommonDetailCode department = new CommonDetailCode();
		department.setComDtId(peronnelDTO.getDeptId());
		department.setComDtNm(peronnelDTO.getDeptName());
		CommonDetailCode position = new CommonDetailCode();
		position.setComDtId(peronnelDTO.getPosId());
		position.setComDtNm(peronnelDTO.getPosName());
		
		//추가한 부분 보안등급 관련
		CommonDetailCode lelvel = new CommonDetailCode();
		lelvel.setComDtId(peronnelDTO.getLevId());
		lelvel.setComDtId(peronnelDTO.getLevName());
		
		//재직현황
		CommonDetailCode status = new CommonDetailCode();
		status.setComDtId(peronnelDTO.getLevId());
		status.setComDtNm(peronnelDTO.getLevName());
		
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
		personnel.setName(peronnelDTO.getName());
		personnel.setName(peronnelDTO.getName());
		personnel.setDepartment(department);
		personnel.setPosition(position);
		personnel.setStatus(status);
		personnel.setLevel(lelvel);
		return personnel;
	}
}