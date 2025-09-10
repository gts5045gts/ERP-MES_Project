package com.erp_mes.mes.pm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Log4j2
@Controller
@RequestMapping("/masterData")
public class ProductBomController {

	@GetMapping("/productBomInfo")
	public String getProductBomInfo() {
		return "pm/product_plan_list";
	}
	
}
