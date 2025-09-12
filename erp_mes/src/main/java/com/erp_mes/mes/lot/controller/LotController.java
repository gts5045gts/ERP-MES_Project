package com.erp_mes.mes.lot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.service.LotService;

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
		
//		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+LotId);
		
		return LotId;
	}
	
	@GetMapping("/testAOP")
	@ResponseBody
	public String testAOP() {
		//lotDTO를 먼저 호출하면서 필수정보 입력
		LotDTO lotDTO = LotDTO.builder()
				.tableName("WAREHOUSE_ITEM")
				.type("process")
				.materialCode("test-STEEL-555")
				.qty(200)
				.build();
				
		String targetId = lotService.registWareHouse(lotDTO);
		//입고등을 insert한후에 pk id값을 받아서 setTargetId하면
		//lotId가 생성됨.
		lotDTO.setTargetId(targetId);
		
		return "ok";
	}
}
