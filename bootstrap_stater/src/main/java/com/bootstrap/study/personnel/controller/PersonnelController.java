package com.bootstrap.study.personnel.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.service.PersonnelService;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/personnel")
@Log4j2
public class PersonnelController {
	private final PersonnelService personnelService;
	
	public PersonnelController(PersonnelService personnelService) {
		this.personnelService = personnelService;
	}
	
	
	@GetMapping("/current")
	public String current(Model model) {
		log.info("PersonnelController current()");
	
		List<Personnel> personList = personnelService.getPersonList(); 
		
		
//		model.addAttribute("personList", personList);
		
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
