package com.erp_mes.mes.purchase.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erp_mes.mes.purchase.service.PurchaseService;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/purchase")
@Log4j2
public class PurchaseController {
	private final PurchaseService purchaseService;

	public PurchaseController(PurchaseService purchaseService) {
		this.purchaseService = purchaseService;
	}

	// 수주 화면
	@GetMapping("purchaseOrder")
	public String order() {

		return "/purchase/purchaseOrder";
	}
}
