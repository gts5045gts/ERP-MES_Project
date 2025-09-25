package com.erp_mes.mes.pop.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.erp.commonCode.dto.CommonCodeDTO;
import com.erp_mes.erp.config.util.SessionUtil;
import com.erp_mes.mes.lot.trace.TrackLot;
import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.mes.pop.dto.DefectDTO;
import com.erp_mes.mes.pop.dto.WorkResultDTO;
import com.erp_mes.mes.pop.entity.WorkResult;
import com.erp_mes.mes.pop.mapper.WorkResultMapper;
import com.erp_mes.mes.pop.service.DefectService;
import com.erp_mes.mes.pop.service.WorkResultService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Controller
@RequestMapping("/pop")
@Log4j2
@RequiredArgsConstructor
public class WorkResultController {
	
	private final WorkResultMapper workResultMapper;
	private final WorkResultService workResultService;
	private final DefectService defectService;
	
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
	@GetMapping("/bom/{productId}")
	@ResponseBody
	public List<WorkResultDTO> getBom(@PathVariable("productId") String productId) {
		return workResultService.workOrderWithBom(productId);
	}
	
	// 작업시작 클릭시 작업현황 업데이트
	@PostMapping("/startWork") 
	@ResponseBody
	@TrackLot(tableName = "work_result", pkColumnName = "work_order_id")
	public List<WorkResultDTO> startWork(@RequestBody WorkResultDTO work) {
		
		HttpSession session = SessionUtil.getSession();
        session.setAttribute("targetIdValue", work.getWorkOrderId());
		
		return workResultService.startWork(work.getWorkOrderId());
	}
	
	// 작업현황 전체 조회(무한스크롤)
	@GetMapping("/workResultList")
	@ResponseBody
	public List<WorkResultDTO> getWorkResultList(
			@RequestParam(value = "page", defaultValue = "0") int page, 
			@RequestParam(value = "size", defaultValue = "20") int size) {
		return workResultService.getPagedWorkResults(page, size);
	}
	
	// 수량 업데이트
	@PostMapping("/workResultUpdate")
	@ResponseBody
	public int workResultUpdate(@RequestBody WorkResultDTO dto) {
		 return workResultService.updateWorkResult(dto);
	}
	
	// 작업완료 
	@PostMapping("/workFinish")
    @ResponseBody
    public void workFinish(@RequestBody Long workOrderId) {
		if (workOrderId != null) {
			workResultMapper.updateWorkStatusFinish(workOrderId);  
		}
	        
    }

	 // 불량 사유 버튼 
	@GetMapping("/defectReason")
	@ResponseBody
	public List<CommonDetailCodeDTO> getDefectReason() {

		return workResultService.getDefectReason();  
	}
	
	// 작업완료 버튼 클릭시 불량 테이블에 추가
	@PostMapping("/saveDefect")
	@ResponseBody
	public void saveDefect(Authentication authentication, @RequestBody Map<String, Object> map) {
		Long resultId = Long.valueOf(map.get("resultId").toString());
		Long workOrderId = Long.valueOf(map.get("workOrderId").toString());
		String empId = authentication.getName();
		
		// Map -> DefectDTO 변환
		ObjectMapper mapper = new ObjectMapper();
		DefectDTO defectDTO = mapper.convertValue(map.get("defectDTO"), DefectDTO.class);
		
		defectDTO.setEmpId(empId);
		defectDTO.setDefectLocation(1L);
		defectDTO.setWorkOrderId(workOrderId);
		
		
		Long defectItemId = defectService.saveDefect(defectDTO);
		
		workResultService.updateDefect(resultId, defectItemId);
	}
	

	
	
}