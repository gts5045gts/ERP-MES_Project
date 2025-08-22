package com.bootstrap.study.personnel.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bootstrap.study.personnel.dto.DepartmentDTO;
import com.bootstrap.study.personnel.dto.PersonnelDTO;
import com.bootstrap.study.personnel.dto.PositionDTO;
import com.bootstrap.study.personnel.entity.Department;
import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.entity.Position;
import com.bootstrap.study.personnel.repository.DepartmentRepository;
import com.bootstrap.study.personnel.repository.PersonnelRepository;
import com.bootstrap.study.personnel.repository.PositionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
@Log4j2
public class PersonnelService {
	
	private final PersonnelRepository personnelRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    // 모든 부서를 조회하여 DTO 리스트로 반환
    public List<DepartmentDTO> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(dept -> {
                    DepartmentDTO dto = new DepartmentDTO();
                    dto.setId(dept.getDeptId());
                    dto.setName(dept.getDeptName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 특정 부서의 직원 목록을 조회하여 DTO 리스트로 반환
    public List<PersonnelDTO> getEmployeesByDepartmentId(Long deptId) {
        List<Personnel> personnels = personnelRepository.findByDepartment_DeptId(deptId);
        return personnels.stream()
                .map(this::convertToDto) // convertToDto 메소드를 사용해 DTO로 변환 - Entity -> DTO 변환 전용 메서드
                .collect(Collectors.toList());
    }
    
    // Employee 엔티티를 PersonnelDTO로 변환하는 private 메소드
    private PersonnelDTO convertToDto(Personnel personnel) {
    	PersonnelDTO dto = new PersonnelDTO();
        dto.setEmpId(personnel.getEmpId()); 
        dto.setDeptName(personnel.getDepartment().getDeptName());
        dto.setPosName(personnel.getPosition().getPosName());
        dto.setName(personnel.getName());
        dto.setPhone(personnel.getPhone());
        dto.setEmail(personnel.getEmail());
        return dto;
    }
    
    // 인사현황 페이지에 필요한 전체 직원 목록을 조회하는 메서드
 	public List<PersonnelDTO> getAllPersonnels() {
 		
 		// Personnel 엔티티 목록을 가져와서 DTO로 변환
 		List<Personnel> personnelList = personnelRepository.findAll();
 		return personnelList.stream()
 				.map(PersonnelDTO::fromEntityCurrent)
 				.collect(Collectors.toList());
 	}
 	
 	public List<PositionDTO> getAllPositions() {
		List<Position> positionList = positionRepository.findAll();

		return positionList.stream().map(result -> {
			PositionDTO dto = new PositionDTO();
			dto.setId(result.getPosId());
			dto.setName(result.getPosName());
			return dto;
		}).collect(Collectors.toList());
	}
 	
 	public void personRegist(PersonnelDTO personnelDTO) {
		Department department = new Department();	// 부서 
		Position position = new Position();			// 직급

		//현재 날짜
		LocalDate today = LocalDate.now();
        // yyyyMMdd 포맷 지정
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");		//ex) 20250821 형태로 저장
		String todayStr = today.format(formatter);									//현재 날짜 String타입으로 저장 
		
		List<Personnel> personnelList = personnelRepository.findAll();
		Long count = (long) (personnelList.size() + 1);								//전체 사원수 +1 ex)2+1 
		String employeeId = todayStr + String.format("%02d", count);				//count 표시 형식 ex) 03
																					//현재날짜 String 타입으로 저장한 변수 + 03 ==> ex) 2025082103
		
		personnelDTO.setEmpId(employeeId);				//부서 아이디 부서타입의 변수에 저장
		position.setPosId(personnelDTO.getPosId());		//직급 아이디 직급타입의 변수에 저장
		
		department.setDeptId(personnelDTO.getDeptId());

		log.info("사원등록 정보: " + personnelDTO.toString());
		
		Personnel personnel = new Personnel();
		personnel.setUpdate(new Timestamp(System.currentTimeMillis()));
		personnel.setEmpId(personnelDTO.getEmpId());
		personnel.setJoinDate(todayStr);
		personnel.setName(personnelDTO.getName());
		personnel.setEmail(personnelDTO.getEmail());
		personnel.setPasswd(personnelDTO.getPasswd());
		personnel.setPhone(personnelDTO.getPhone());
		personnel.setDepartment(department);
		personnel.setPosition(position);
		personnelRepository.save(personnel);

	}

}