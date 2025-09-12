package com.erp_mes.mes.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erp_mes.mes.stock.service.InvService;
import com.erp_mes.mes.stock.service.WareService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2  
@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class WareController {
	
	private final InvService invService;
	private final WareService wareService;
	
	//창고내 재고 현황
	@GetMapping("/warehouse")
    public String warehouseList(Model model) {
        log.info("창고 현황 페이지 접속");
        return "inventory/warehouse_list";
    }
	
	//창고 정보 관리
	@GetMapping("/ware")
    public String wareList(Model model) {
        log.info("창고 현황 페이지 접속");
        return "inventory/ware_list";
    }
	
}
