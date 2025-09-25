package com.erp_mes.mes.pm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.mes.pm.dto.BomDTO;
import com.erp_mes.mes.pm.dto.ProductDTO;
import com.erp_mes.mes.pm.service.ProductBomService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



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
	public String getProductBomInfo(Model model) {
		
		List<ProductDTO> productDTOList = productBomService.getProductList();
		model.addAttribute("productDTOList", productDTOList);
		log.info(" >>>>>>>>>>>>>>>>>>>>>>>>>>> productDTOList : " + productDTOList);
		
		// 제품유형(공통코드)
		List<CommonDetailCodeDTO> commonPtype = productBomService.getCommonPtype();
		log.info(" >>>>>>>>>>>>>>>>>>>>>>>>>>> commonPtype : " + commonPtype);
		model.addAttribute("commonPtype", commonPtype);
		
		// 단위(공통코드)
		List<CommonDetailCodeDTO> commonUnit = productBomService.getCommonUnit();
		log.info(" >>>>>>>>>>>>>>>>>>>>>>>>>>> commonUnit : " + commonUnit);
		model.addAttribute("commonUnit", commonUnit);
		
		return "pm/product_bom_list";
	}
	
	@ResponseBody
	@GetMapping("/productList")
	public List<ProductDTO> getProductList() {
		return productBomService.getProductList(); // json으로 변환 => 그리드에 값 넣어야해서
	}
	
	// 제품 등록
	@ResponseBody
	@PostMapping("/productRegist")
	public ResponseEntity<String> productRegist(@RequestBody ProductDTO productDTO) {
		productBomService.productRegist(productDTO);
	    return ResponseEntity.ok("success");
	}
	
	// bom 리스트
	@ResponseBody
	@GetMapping("/bomList")
	public List<BomDTO> getBomList(@RequestParam(name = "product_id") String productId) {
		return productBomService.getBomList(productId); // json으로 변환 => 그리드에 값 넣어야해서
	}
	
	// bom 등록
	@ResponseBody
	@PostMapping("/bomRegist")
	public String registBom(@RequestBody BomDTO bomDTO) {
		
		log.info(">>> bomId=" + bomDTO.getBomId());
	    log.info(">>> productId=" + bomDTO.getProductId());
	    log.info(">>> revisionNo=" + bomDTO.getRevisionNo());
	    log.info(">>> materialIds=" + bomDTO.getMaterials());
		
	    productBomService.insertBomList(bomDTO);

	    return "success";
	}
}











