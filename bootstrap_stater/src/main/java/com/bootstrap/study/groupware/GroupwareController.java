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
	
	@GetMapping("/gwMain")
	public String gwMain() {
		log.info("PersonnelController orgChart()");
		
		return "/gw/gwMain";
	}
	@GetMapping("/aNotice")
	public String regist() {
		log.info("PersonnelController regist()");
		
		return "/gw/aNotice";
	}
	
}
