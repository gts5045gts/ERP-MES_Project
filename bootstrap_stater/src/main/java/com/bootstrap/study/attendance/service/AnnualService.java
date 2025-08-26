package com.bootstrap.study.attendance.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.bootstrap.study.attendance.dto.AnnualDTO;
import com.bootstrap.study.attendance.entity.Annual;
import com.bootstrap.study.attendance.repository.AnnualRepository;
import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.repository.PersonnelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnualService {


	private final AnnualRepository annRepository;
	private final PersonnelRepository empRepository;
	

// ============================================	
	
	// 내 연차 조회
	public AnnualDTO myAnnual(String empId, String annYear) {
		Annual ann = annRepository.findByEmpIdAndAnnYear(empId, annYear)
				.orElseThrow(() -> new RuntimeException("해당 사원의 연차가 없습니다."));
		
		
		Personnel emp = empRepository.findById(empId)
	            .orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));
		
		return new AnnualDTO(ann, emp);
		
	}





	
	

	
}
