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
		//lotDTO에 저장하고 아무 service 실행하면서 lotDTO만 가져가준다.
		//targetId를 넣으려면 입고 시키고 id받아서 진행해야함 해결해야됨
		LotDTO lotDTO = new LotDTO();
		lotDTO.setTargetId("122");
		lotDTO.setTableName("WAREHOUSE_ITEM");
		lotDTO.setType("process");
		lotDTO.setMaterialCode("CUT-STEEL-123");
		lotDTO.setQty(100);
		lotService.registLot(lotDTO);
		
		return "ok";
	}
}
