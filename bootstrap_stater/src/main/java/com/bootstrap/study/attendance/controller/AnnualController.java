package com.bootstrap.study.attendance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bootstrap.study.attendance.service.AnnualService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequestMapping("/attendance")
@Log4j2
@RequiredArgsConstructor
public class AnnualController {
	
	private final AnnualService annService;
	
	
// ============================================================
	
	// 화면이동
	@GetMapping("/annualList")
	public String annualList() {
		return "commute/annual_list";
	}
	

	// 내 연차 조회하기 
	@GetMapping("/myAnnList/{empId}")
	public String myAnnList(@PathVariable("empId") String empId, Model model) {
		model.addAttribute("myAnn", annService.findByEmpId(empId));
		return "commute/annual_list";
	}


}
