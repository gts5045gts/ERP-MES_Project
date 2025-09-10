package com.erp_mes.mes.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erp_mes.mes.stock.service.InvService;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;  
@Log4j2  
@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InvController {
    
	private final InvService invService;
	
	// 재고 현황
	@GetMapping("/stock")
	public String stockList(Model model) {
	    log.info("재고 현황 페이지 접속");
	    return "inventory/stock_list";
	}
	
	// 출고 관리
	@GetMapping("/outbound")
	public String outboundList(Model model) {
	    log.info("출고 관리 페이지 접속");
	    return "inventory/outbound_list";
	}
}
