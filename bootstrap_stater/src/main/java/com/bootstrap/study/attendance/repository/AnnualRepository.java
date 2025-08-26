package com.bootstrap.study.attendance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bootstrap.study.attendance.entity.Annual;

@Repository
public interface AnnualRepository extends JpaRepository<Annual, Long> {

	
	// 내 연차 조회
	Optional<Annual> findByEmpIdAndAnnYear(String empId, String annYear);
	
	

}
