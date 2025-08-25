package com.bootstrap.study.approval.dto;

import java.sql.Timestamp;

import com.bootstrap.study.personnel.entity.Personnel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
//import oracle.sql.TIMESTAMP;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ApprEmpDTO {

	private String empId;		// 사원id 		ex)2025082101
	private String name;		// 이름
	private String passwd;		// 비밀번호
	private String resident;	// 주민등록번호
	private String addrNum;		// 우편번호
	private String addr1;		// 주소
	private String addr2;		// 상세주소
	private String email;		// 이메일
	private String phone;		// 전화번호
	private String joinDate;	// 입사일
	private String resignDate;	// 퇴사일	
	private Timestamp update;	// 수정일
	private String status;		// 재직상태
	

	// 부서 직급 정보
	private Long posId;			// 직급
	private String posName;		
	private Long deptId;		// 부서명은 엔티티에서 직접 가져오지 않고 DTO에서 추가
	private String deptName;   	

	// Entity -> DTO 변환을 위한 정적 팩토리 메서드
	public static ApprEmpDTO fromEntity(Personnel personnel) {
		return ApprEmpDTO.builder().empId(personnel.getEmpId()).name(personnel.getName()).passwd(personnel.getPasswd())
				.resident(personnel.getResident()).addrNum(personnel.getAddrNum()).addr1(personnel.getAddr1()).addr2(personnel.getAddr2())
				.email(personnel.getEmail()).phone(personnel.getPhone()).joinDate(personnel.getJoinDate()).resignDate(personnel.getResignDate())
				.update(personnel.getUpdate())
				.deptName(personnel.getDepartment() != null ? personnel.getDepartment().getDeptName()  : null)
				.deptId(personnel.getDepartment() != null ? personnel.getDepartment().getDeptId() : null)
				.posId(personnel.getPosition() != null ? personnel.getPosition().getPosId() : null)
				.posName(personnel.getPosition() != null ? personnel.getPosition().getPosName() : null)
				.build();
	}
}