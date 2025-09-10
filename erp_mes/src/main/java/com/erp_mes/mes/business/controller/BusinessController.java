package com.erp_mes.mes.business.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;


@Controller
@RequestMapping("/business")
@Log4j2
public class BusinessController {
	
	// 거래처
	@GetMapping("client")
	public String client() {
		
		
		return "/business/client";
	}

}
