package com.bootstrap.study.personnel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bootstrap.study.personnel.entity.Personnel;


public interface PersonnelRepository extends JpaRepository<Personnel, String> {
	// 부서 ID로 직원 조회
	List<Personnel> findByDepartment_ComDtId(String deptId);
	
	// 로그인할때 department와 level 필드를 함께 조회
    @EntityGraph(attributePaths = {"department", "level"})
    Optional<Personnel> findByEmpId(String empId);
}
