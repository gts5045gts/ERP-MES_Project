package com.erp_mes.mes.pm.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.erp_mes.mes.pm.dto.ProductDTO;
import com.erp_mes.mes.pm.service.ProductBomService;


@Log4j2
@Controller
@RequestMapping("/masterData")
public class ProductBomController {

	private final ProductBomService productBomService;
	
	public ProductBomController(ProductBomService productBomService) {
		this.productBomService = productBomService;
	}
	
	// 제품 리스트 정보 페이지
	@GetMapping("/productBomInfo")
	public String getProductBomInfo() {
		
		List<ProductDTO> productDTOList = productBomService.getProductList();
		log.info("productDTOList : " + productDTOList);
		
		return "pm/product_plan_list";
	}
	
}
