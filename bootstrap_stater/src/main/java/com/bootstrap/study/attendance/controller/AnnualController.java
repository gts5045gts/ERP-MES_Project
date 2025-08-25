package com.bootstrap.study.attendance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/attendance")
@Log4j2
@RequiredArgsConstructor
public class AnnualController {
	
	
	// 화면이동
	@GetMapping("/annualList")
	public String annualList() {
		return "commute/annual_list";
	}
	

}
