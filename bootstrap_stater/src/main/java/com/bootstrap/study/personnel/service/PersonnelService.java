package com.bootstrap.study.personnel.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bootstrap.study.personnel.dto.DepartmentDTO;
import com.bootstrap.study.personnel.dto.PersonnelDTO;
import com.bootstrap.study.personnel.entity.Department;
import com.bootstrap.study.personnel.entity.Personnel;

import com.bootstrap.study.personnel.repository.DepartmentRepository;
import com.bootstrap.study.personnel.repository.PersonnelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
public class PersonnelService {

	private final PersonnelRepository personnelRepository;
	private final DepartmentRepository departmentRepository;

	// 모든 부서를 조회하여 DTO 리스트로 반환
	public List<DepartmentDTO> getAllDepartments() {
		List<Department> departments = departmentRepository.findAll();
		return departments.stream().map(dept -> {
			DepartmentDTO dto = new DepartmentDTO();
			dto.setId(dept.getDeptId());
			dto.setName(dept.getDeptName());
			return dto;
		}).collect(Collectors.toList());
	}

	// 특정 부서의 직원 목록을 조회하여 DTO 리스트로 반환
	public List<PersonnelDTO> getEmployeesByDepartmentId(Long deptId) {
		List<Personnel> personnels = personnelRepository.findByDepartment_DeptId(deptId);
		return personnels.stream().map(this::convertToDto) // convertToDto 메소드를 사용해 DTO로 변환 - Entity -> DTO 변환 전용 메서드
				.collect(Collectors.toList());
	}

	// Employee 엔티티를 EmployeeDTO로 변환하는 private 메소드
	private PersonnelDTO convertToDto(Personnel personnel) {
		PersonnelDTO dto = new PersonnelDTO();
		dto.setEmpId(personnel.getEmpId());
		dto.setDeptName(personnel.getDepartment().getDeptName());
		dto.setPosition(personnel.getPosition());
		dto.setName(personnel.getName());
		dto.setPhone(personnel.getPhone());
		dto.setEmail(personnel.getEmail());
		return dto;
	}

}
