package com.bootstrap.study.attendance.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootstrap.study.attendance.dto.CommuteDTO;
import com.bootstrap.study.attendance.service.CommuteService;

@Controller
@RequestMapping("/attendance")
public class CommuteController {

	private final CommuteService commuteService;
	private final String emp_id = "2025081901"; 
	
	public CommuteController(CommuteService commuteService) {
		this.commuteService = commuteService;
	}
// =====================================================================	
		
	// 출퇴근관리 리스트
	@GetMapping("/commuteList")
	public String getComuuteList(@RequestParam(name = "date", required = false) String date, Model model) {
		String  queryDate = (date != null) ? date : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
		List<CommuteDTO> commuteDTOList = commuteService.getDeptCommuteList(emp_id, queryDate);
		model.addAttribute("commuteDTOList", commuteDTOList);
		model.addAttribute("queryDate", queryDate);
		model.addAttribute("empId", emp_id);

		System.out.println("commuteDTOList : " + commuteDTOList);		
		System.out.println("queryDate : " + queryDate);		

		return "/commute/commute_list";
	}

	// 출근버튼
	@ResponseBody
	@PostMapping("/checkIn")
	public CommuteDTO checkIn() {
		return commuteService.checkIn(emp_id);
	}

	// 내 근태내역 관리
	@GetMapping("/attendanceList")
	public String getAttendanceList() {

		return "/commute/table";
	}

}
