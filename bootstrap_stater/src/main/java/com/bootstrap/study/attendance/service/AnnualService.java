package com.bootstrap.study.attendance.service;


import org.springframework.stereotype.Service;

import com.bootstrap.study.attendance.repository.AnnualRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnualService {


	private final AnnualRepository annRepository;
	
	
// ============================================	
	
	public Object findByEmpId(String empId) {
		// TODO Auto-generated method stub
		return null;
	}
	

	
}
