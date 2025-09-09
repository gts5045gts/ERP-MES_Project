package com.erp_mes.mes.plant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/plant")
@RequiredArgsConstructor
@Log4j2
public class ProcessController {

	
	@GetMapping("/process")
	public String process() {
		log.info("완성");
		
		
		return "/plant/process";
	}
}
