package com.bootstrap.study.personnel.dto;

import com.bootstrap.study.personnel.entity.Personnel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PersonnelDTO {
	// private Long id;
	private String empId;
	private Long empCd;
	private String name;
	private String passwd;
	private Long position;
	private String phone;
	private String email;
	private String deptName; // 부서명은 엔티티에서 직접 가져오지 않고 DTO에서 추가

	private Long deptId;

	// Entity -> DTO 변환을 위한 정적 팩토리 메서드
	public static PersonnelDTO fromEntity(Personnel personnel) {
		return PersonnelDTO.builder().empId(personnel.getEmpId()).empCd(personnel.getEmpCd()).passwd(personnel.getPasswd()).name(personnel.getName())
				.position(personnel.getPosition()).phone(personnel.getPhone()).email(personnel.getEmail())
				.deptName(personnel.getDepartment() != null ? personnel.getDepartment().getDeptName() : null).build();
	}
}
