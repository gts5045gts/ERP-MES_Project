package com.bootstrap.study.personnel.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bootstrap.study.personnel.entity.Personnel;


public interface PersonnelRepository extends JpaRepository<Personnel, String> {
	// 부서 ID로 직원 조회
	List<Personnel> findByDepartment_DeptId(Long deptId);
}
