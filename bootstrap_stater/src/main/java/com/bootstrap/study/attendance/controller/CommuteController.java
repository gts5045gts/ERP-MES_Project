package com.bootstrap.study.attendance.controller;

import java.sql.Date;
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

import com.bootstrap.study.attendance.dto.AdminCommuteDTO;
import com.bootstrap.study.attendance.dto.CommuteDTO;
import com.bootstrap.study.attendance.dto.CommuteDeleteLogDTO;
import com.bootstrap.study.attendance.dto.CommuteScheduleDTO;
import com.bootstrap.study.attendance.service.CommuteService;
import com.bootstrap.study.commonCode.dto.CommonDetailCodeDTO;
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
	public String getComuuteList(Model model,
	        @RequestParam(name = "startDate", required = false) String startDate,
	        @RequestParam(name = "endDate", required = false) String endDate) {
		
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
	    // ==============================================================================
		
	    // 기본값은 오늘
	    LocalDate today = LocalDate.now();
	    if (startDate == null || startDate.isEmpty()) startDate = today.toString();
	    if (endDate == null || endDate.isEmpty()) endDate = today.toString();
	    
	    
	    Map<String, Object> paramMap = new HashMap<>();
	    paramMap.put("empId", empId);
	    paramMap.put("startDate", startDate);
	    paramMap.put("endDate", endDate);
	    
//		System.out.println("startDate : " + startDate);		
//		System.out.println("endDate : " + endDate);		
		
		List<CommuteDTO> commuteDTOList = commuteService.getDeptCommuteList(paramMap);
		model.addAttribute("commuteDTOList", commuteDTOList);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);

		System.out.println("commuteDTOList : " + commuteDTOList);		

		return "/commute/commute_list";
	}

	// 출근버튼
	@ResponseBody
	@PostMapping("/checkIn")
	public ResponseEntity<CommuteDTO> checkIn() {
		
	    // 로그인한 사용자 객체 꺼내기
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
	        // 로그인 안 된 경우 401 Unauthorized 반환
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    String empId = userDetails.getUsername(); // usernameParameter("empId") 값 그대로 들어옴
//	    System.out.println("로그인 사용자 ID: " + empId);

	    // 출근 처리
	    try {
	        CommuteDTO commuteCheckIn = commuteService.checkIn(empId);
//	        System.out.println("commuteCheckIn : " + commuteCheckIn);
	        return ResponseEntity.ok(commuteCheckIn);
	    } catch (IllegalStateException e) {
	        // 이미 출근 기록 있을 경우 409 Conflict 반환
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
	    }
	}
	
	// 퇴근버튼
	@ResponseBody
	@PostMapping("/checkOut")
	public ResponseEntity<CommuteDTO> checkOut() {
		
		// 로그인한 사용자 객체 꺼내기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
			// 로그인 안 된 경우 401 Unauthorized 반환
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String empId = userDetails.getUsername(); // usernameParameter("empId") 값 그대로 들어옴
//		System.out.println("로그인 사용자 ID: " + empId);
		
		// 퇴근 처리
		try {
			CommuteDTO commuteCheckOut = commuteService.checkOut(empId);
//			System.out.println("commuteCheckOut : " + commuteCheckOut);
			return ResponseEntity.ok(commuteCheckOut);
		} catch (IllegalStateException e) {
			// 이미 출근 기록 있을 경우 409 Conflict 반환
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		}
	}

	// 관리자 근태관리
	@GetMapping("/adminCommute")
	public String getAttendanceList(@RequestParam(name = "startDate", required = false) String startDate,
									@RequestParam(name = "endDate", required = false) String endDate,
									@RequestParam(name = "deptId", required = false) String deptId,
									Model model) {
		
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
	        return "/commute/commute_list"; // 추후에 메인사이트로 이동하게끔 변경
	    }
	    // ==============================================================================
	    
		// 공통코드로 된 해당 부서인원 값 조회
		List<CommonDetailCodeDTO> commonDept = commuteService.getCommonDept();
		System.out.println("commonDept : " + commonDept);
		CommonDetailCodeDTO allDept = new CommonDetailCodeDTO();
		allDept.setComDtId("ALL");
		allDept.setComDtNm("전체부서");
		commonDept.add(0, allDept); // 셀렉박스의 리스트의 맨 앞(0번 인덱스)에 allDept를 넣기
		
		model.addAttribute("commonDept", commonDept);
		model.addAttribute("selectedDept", deptId != null ? deptId : "ALL"); // 부서 셀렉박스 고정값
		
	    // 오늘 기준으로 값 들고오기
	    LocalDate today = LocalDate.now();
	    if (startDate == null || startDate.isEmpty()) startDate = today.toString();
	    if (endDate == null || endDate.isEmpty()) endDate = today.toString();
	    
	    Map<String, Object> paramMap = new HashMap<>();
	    paramMap.put("empId", empId);
	    paramMap.put("startDate", Date.valueOf(startDate));
	    paramMap.put("endDate", Date.valueOf(endDate));
	    paramMap.put("deptId", deptId);
		
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
//		System.out.println("startDate : " + startDate);
//		System.out.println("endDate : " + endDate);
//		System.out.println("deptId : " + deptId);
		
		// 공통코드로된 근무상태 조회
		List<CommonDetailCodeDTO> commonStatus = commuteService.getCommonStatus();
		model.addAttribute("commonStatus", commonStatus);
		
		// 부서 조회
	    List<AdminCommuteDTO> adminCommuteList;
	    if(deptId == null || deptId.isEmpty() || "ALL".equals(deptId)) {
	    	adminCommuteList = commuteService.getAllDeptCommuteList(paramMap); // 전체 부서 조회
	    } else {
	    	adminCommuteList = commuteService.getSpecificDeptCommuteList(paramMap); // 특정 부서 조회
	    }
	    model.addAttribute("adminCommuteList", adminCommuteList);
	    System.out.println("adminCommuteList :" + adminCommuteList);
		
	    
		return "/commute/admin_commute_list";
	}
	
	// 수정버튼
	@ResponseBody
	@PostMapping("/updateWorkStatus")
	public ResponseEntity<Map<String, Object>> updateWorkStatus(@RequestBody List<AdminCommuteDTO> updateList) {
		
		// 로그인한 사용자 객체 꺼내기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
			// 로그인 안 된 경우 401 Unauthorized 반환
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
//		System.out.println("로그인 사용자 ID: " + empId);
		
		// ================================================================================
		
		// 수정버튼 처리
		int updateCount = commuteService.updateWorkStatus(updateList);
	    Map<String, Object> result = new HashMap<>();
	    result.put("message", updateCount + "건 업데이트 완료");
	    return ResponseEntity.ok(result);
		
	}
	
	// 출근기록 삭제
	@ResponseBody
	@PostMapping("/deleteCommuteRecord")
	public ResponseEntity<CommuteDeleteLogDTO> deleteWork() {
		
		// 로그인한 사용자 객체 꺼내기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
			// 로그인 안 된 경우 401 Unauthorized 반환
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String empId = userDetails.getUsername(); // usernameParameter("empId") 값 그대로 들어옴
//		System.out.println("로그인 사용자 ID: " + empId);
		
		// 퇴근 처리
//		try {
//			CommuteDTO commuteCheckOut = commuteService.checkOut(empId);
////			System.out.println("commuteCheckOut : " + commuteCheckOut);
//			return ResponseEntity.ok(commuteCheckOut);
//		} catch (IllegalStateException e) {
//			// 이미 출근 기록 있을 경우 409 Conflict 반환
//			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
//		}
		return null;
	}
	

}