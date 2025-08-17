package com.bootstrap.study.groupware;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bootstrap.study.personnel.controller.PersonnelController;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/groupware")
@Log4j2
public class GroupwareController {
	
	@GetMapping("/notice")
	public String notice() {
		log.info("GroupwareController gwMain()");
		
		return "/gw/notice";
	}
	
	@GetMapping("/schedule")
	public String schedule() {
		log.info("GroupwareController aSchedule()");
		
		return "/gw/schedule";
	}
	
	@GetMapping("/ntcWrite")
	public String ntcWrite() {
		log.info("GroupwareController ntcWrite()");
		
		return "/gw/ntcWrite";
	}
	
	@GetMapping("/schWrite")
	public String schWrite() {
		log.info("GroupwareController schWrite()");
		
		return "/gw/schWrite";
	}
	
	
	@GetMapping("/document")
	public String document() {
		log.info("GroupwareController document()");
		
		return "/gw/document";
	}
	
	@GetMapping("/docWrite")
	public String docWrite() {
		log.info("GroupwareController docWrite()");
		
		return "/gw/docWrite";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
