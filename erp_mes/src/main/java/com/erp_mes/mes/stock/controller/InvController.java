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
@RequiredArgsConstructor
public class InvController {
    
	private final InvService invService;
	
	// 재고 현황
	@GetMapping("/inventory/stock")
	public String stockList(Model model) {
	    log.info("재고 현황 페이지 접속");
	    return "inventory/stock_list";
	}
	
	// 입고 관리
	@GetMapping("/purchase/goods")
	public String inboundList(Model model) {
	    log.info("입고 관리 페이지 접속");
	    return "inventory/inbound_list";
	}
	
	// 출고 관리
	@GetMapping("/inventory/outbound")
	public String outboundList(Model model) {
	    log.info("출고 관리 페이지 접속");
	    return "inventory/outbound_list";
	}
	
	// 기준정보관리 - 자재정보(소재)
	@GetMapping("/inventory/material")
	public String materialList(Model model) {
	    log.info("출고 관리 페이지 접속");
	    return "inventory/material_list";
	}
	
	// 기준정보관리 - 제품정보(완제품/반제품)
	@GetMapping("/inventory/item")
	public String itemList(Model model) {
	    log.info("출고 관리 페이지 접속");
	    return "inventory/item_list";
	}
}
