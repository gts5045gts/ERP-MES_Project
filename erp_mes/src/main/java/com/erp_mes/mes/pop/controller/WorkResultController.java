package com.erp_mes.mes.pop.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.mes.pop.dto.WorkResultDTO;
import com.erp_mes.mes.pop.entity.WorkResult;
import com.erp_mes.mes.pop.mapper.WorkResultMapper;
import com.erp_mes.mes.pop.service.WorkResultService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;





@Controller
@RequestMapping("/pop")
@Log4j2
@RequiredArgsConstructor
public class WorkResultController {
	
	private final WorkResultMapper workResultMapper;
	
	
// ===================================================================
	
	
	// 화면이동
	@GetMapping("/workResult")
	public String workResultList() {
		return "pop/work_result";
	}
	
	// 작업지시 가져오기 
	@GetMapping("/workOrder")
	@ResponseBody
	public List<WorkResultDTO> getWorkOrder(Authentication authentication) {
		String empId = authentication.getName();
		List<WorkResultDTO> list = workResultMapper.workerkWithOrder(empId);

		return list;
	}
	
	// 작업지시 클릭시 해당 bom 조회
	@GetMapping("/bom/{workOrderId}")
	@ResponseBody
	public List<WorkResultDTO> getBom(@PathVariable("workOrderId") Long workOrderId) {
		List<WorkResultDTO> list = workResultMapper.workOrderWithBom(workOrderId);
		return list;
	}
	
	// 작업시작 체크박스 클릭시 작업현황 업데이트
	@PostMapping("/workList") 
	@ResponseBody
	public List<WorkResultDTO> workList(@RequestBody List<Long> workOrderIds) {
		List<WorkResultDTO> list = workResultMapper.workResultWithBom(workOrderIds);
		return list;
	}
	
	
	
}