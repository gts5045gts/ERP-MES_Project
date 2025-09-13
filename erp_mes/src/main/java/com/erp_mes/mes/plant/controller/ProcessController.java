package com.erp_mes.mes.plant.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.mes.plant.service.ProcessService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/plant")
@RequiredArgsConstructor
@Log4j2
public class ProcessController {
	
	final private CommonDetailCodeRepository codeRepository;
	final private ProcessService proService ;
	
	@GetMapping("/process")
	public String process() {
		log.info("완성");
		
		
		return "/plant/process";
	}
	
	@ResponseBody
	@GetMapping("/processGrid")
	public List<Map<String, String>> processGrid(){
		List<Map<String, String>> proList = proService.findAll();
		log.info("list" + proList.toString()); 
		
		
		return proList;
	}
}
