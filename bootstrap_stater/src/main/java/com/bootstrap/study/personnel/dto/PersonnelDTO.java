package com.bootstrap.study.personnel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelDTO {
	private Long id;
    private String empNo;
    private String name;
    private String position;
    private String phone;
    private String email;
    private String deptName; // 부서명은 엔티티에서 직접 가져오지 않고 DTO에서 추가

    // Entity -> DTO 변환을 위한 정적 팩토리 메서드
//    public static PersonnelDto fromEntity(Employee employee) {
//        return PersonnelDto.builder()
//                .id(employee.getId())
//                .empid(employee.getEmpId())
//                .name(employee.getName())
//                .position(employee.getPosition())
//                .phone(employee.getPhone())
//                .email(employee.getEmail())
//                .deptId(employee.getDeptId() != null ? employee.getDeptId().getName() : null)
//                .build();
//    }
}
