package com.bootstrap.study.attendance.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootstrap.study.attendance.dto.CommuteDTO;
import com.bootstrap.study.attendance.service.CommuteService;

@Controller
@RequestMapping("/attendance")
public class CommuteController {

	private final CommuteService commuteService;
	private final String empId = "2025082229"; 
	
	public CommuteController(CommuteService commuteService) {
		this.commuteService = commuteService;
	}
// =====================================================================	
		
	// 출퇴근관리 리스트
	@GetMapping("/commuteList")
	public String getComuuteList(Model model) {
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
	    Map<String, Object> paramMap = new HashMap<>();
	    paramMap.put("empId", empId);
	    paramMap.put("today", today);
		
		List<CommuteDTO> commuteDTOList = commuteService.getDeptCommuteList(paramMap);
		model.addAttribute("commuteDTOList", commuteDTOList);

		System.out.println("commuteDTOList : " + commuteDTOList);		
		System.out.println("today : " + today);		

		return "/commute/commute_list";
	}

	// 출근버튼
	@ResponseBody
	@PostMapping("/checkIn")
	public CommuteDTO checkIn(@RequestBody Map<String, String> request) {
		String empId = request.get("empId");
		return commuteService.checkIn(empId);
	}

	// 내 근태내역 관리
	@GetMapping("/attendanceList")
	public String getAttendanceList() {

		return "/commute/table";
	}

}
