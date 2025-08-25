package com.bootstrap.study.attendance.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.bootstrap.study.personnel.dto.PersonnelDTO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/attendance")
public class CommuteController {

	private final CommuteService commuteService;
	
	public CommuteController(CommuteService commuteService) {
		this.commuteService = commuteService;
	}
// =====================================================================	
		
	// 출퇴근관리 리스트
	@GetMapping("/commuteList")
	public String getComuuteList(Model model) {
		
		// 로그인한 사용자 객체 꺼내기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    String empId = null; // 변수 초기화

	    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
	        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	        empId = userDetails.getUsername(); // usernameParameter("empId") 값 그대로 들어옴
	        System.out.println("로그인 사용자 ID: " + empId);
	    } else {
	        // 로그인 안 된 상태라면 로그인 페이지로
//	        return "redirect:/login";
	        return "/commute/commute_list";
	    }
		
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
	public ResponseEntity<CommuteDTO> checkIn(@RequestBody Map<String, String> request) {
		
	    // 로그인한 사용자 객체 꺼내기
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
	        // 로그인 안 된 경우 401 Unauthorized 반환
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    String empId = userDetails.getUsername(); // usernameParameter("empId") 값 그대로 들어옴
	    System.out.println("로그인 사용자 ID: " + empId);

	    // 출근 처리
	    try {
	        CommuteDTO commuteCheckIn = commuteService.checkIn(empId);
	        System.out.println("commute : " + commuteCheckIn);
	        return ResponseEntity.ok(commuteCheckIn);
	    } catch (IllegalStateException e) {
	        // 이미 출근 기록 있을 경우 409 Conflict 반환
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
	    }
	}
	
	// 퇴근버튼
	@ResponseBody
	@PostMapping("/checkOut")
	public ResponseEntity<CommuteDTO> checkOut(@RequestBody Map<String, String> request) {
		
		// 로그인한 사용자 객체 꺼내기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
			// 로그인 안 된 경우 401 Unauthorized 반환
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String empId = userDetails.getUsername(); // usernameParameter("empId") 값 그대로 들어옴
		System.out.println("로그인 사용자 ID: " + empId);
		
		// 퇴근 처리
		try {
			CommuteDTO commuteCheckOut = commuteService.checkOut(empId);
			System.out.println("commute : " + commuteCheckOut);
			return ResponseEntity.ok(commuteCheckOut);
		} catch (IllegalStateException e) {
			// 이미 출근 기록 있을 경우 409 Conflict 반환
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		}
	}

	// 내 근태내역 관리
	@GetMapping("/attendanceList")
	public String getAttendanceList() {

		return "/commute/table";
	}

}
