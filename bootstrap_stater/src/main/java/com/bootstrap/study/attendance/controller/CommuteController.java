package com.bootstrap.study.attendance.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bootstrap.study.attendance.dto.CommuteDTO;
import com.bootstrap.study.attendance.service.CommuteService;


@Controller
@RequestMapping("/attendence")
public class CommuteController {

	private final CommuteService commuteService;
	
	public CommuteController(CommuteService commuteService) {
		this.commuteService = commuteService;
	}
// =====================================================================	

	// 출퇴근관리 리스트
	@GetMapping("/commuteList")
	public String getComuuteList(Model model) {
		
		List<CommuteDTO> commuteDTOList = commuteService.getCommuteList();
		
		model.addAttribute("commuteDTOList", commuteDTOList);
		
		System.out.println(commuteDTOList);
		
		return "/commute/commute_list";
	}
	
	// 내 근태내역 관리
	@GetMapping("/attendanceList")
	public String getAttendanceList() {
		
		return "/commute/table";
	}
	
}
