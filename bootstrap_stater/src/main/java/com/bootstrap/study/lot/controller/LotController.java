package com.bootstrap.study.lot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootstrap.study.lot.entity.LotMaster;
import com.bootstrap.study.lot.service.LotService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/lot")
@Log4j2
@RequiredArgsConstructor
public class LotController {
	
	private final LotService lotService;
	
	@GetMapping("/generate")
	@ResponseBody
	public ResponseEntity<String> postMethodName(@RequestParam(name = "domain") String domain, @RequestParam(name = "qty", required = false) Integer qty, @RequestParam(name = "machineId", required = false) String machineId) {
		ResponseEntity<String> LotId = ResponseEntity.ok(lotService.generateLotId(domain, qty, machineId)); 
		
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+LotId);
		
		return LotId;
	}
}
