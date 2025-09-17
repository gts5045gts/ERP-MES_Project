package com.erp_mes.mes.plant.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.mes.plant.dto.ProcessDTO;
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
	@GetMapping("/process-newForm")
	public String process_newFrom(Model model) {
		
		List<CommonDetailCodeDTO> comList = proService.findAllByPro();
		
		
		
		model.addAttribute("comList", comList);
		
		return "/plant/process-newForm";
	}
	
	@ResponseBody
	@GetMapping("/processGrid")
	public List<Map<String, Object>> processGrid(){
		List<Map<String, Object>> proList = proService.findAll();
		log.info("proList" + proList.toString()); 
		
		
		return proList;
	}
	
	@ResponseBody
	@PostMapping("/processAdd")
	public ResponseEntity<String> processAdd(ProcessDTO proDTO){
		log.info("공정 데이터를 전송합니다." + proDTO);
		
		proService.savePro(proDTO);
		
		return ResponseEntity.ok("success");
	}
}
