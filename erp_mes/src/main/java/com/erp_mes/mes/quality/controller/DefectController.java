package com.erp_mes.mes.quality.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;


@Controller
@RequestMapping("/defect")
@Log4j2
public class DefectController {
	
	@GetMapping("")
	public String getMethodName() {
		return "/qc/defect";
	}
	

}
