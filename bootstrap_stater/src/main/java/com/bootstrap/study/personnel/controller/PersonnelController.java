package com.bootstrap.study.personnel.controller;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/personnel")
@Log4j2
public class PersonnelController {
	@GetMapping("/current")
	public String current() {
		log.info("PersonnelController current()");
		
		return "/hrn/personnelCurrent";
	}
	
	@GetMapping("/regist")
	public String regist() {
		log.info("PersonnelController regist()");
		
		return "/hrn/personnelRegist";
	}
	
	@GetMapping("/app")
	public String app() {
		log.info("PersonnelController app()");
		
		return "/hrn/personnelApp";
	}
	
	@GetMapping("/orgChart")
	public String orgChart() {
		log.info("PersonnelController orgChart()");
		
		return "/hrn/orgChart";
	}
}
