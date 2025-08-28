package com.bootstrap.study.attendance.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootstrap.study.approval.constant.ApprStatus;
import com.bootstrap.study.approval.entity.Appr;
import com.bootstrap.study.attendance.dto.AnnualDTO;
import com.bootstrap.study.attendance.entity.Annual;
import com.bootstrap.study.attendance.service.AnnualService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping("/attendance")
@Log4j2
@RequiredArgsConstructor
public class AnnualController {
	
	private final AnnualService annService;
	
	
// ============================================================
	
	
	// 화면 + 내 연차 조회하기 
	@GetMapping("/annualList")
	public String annualList(@RequestParam(value="year", required=false) String annYear, Model model,
            Authentication authentication) {
		
		String empId = authentication.getName(); // 로그인한 사원 empId
	    if(annYear == null) {
	        annYear = String.valueOf(java.time.LocalDate.now().getYear()); // 기본: 올해
	    }
		
	    annService.AnnUpdate();
	    
		AnnualDTO myAnn = annService.myAnnual(empId, annYear);
		model.addAttribute("myAnn", myAnn);
		return "commute/annual_list";
	}
	
	// 내 사용률(도넛 차트)
	@GetMapping("/annualList/chart")
	@ResponseBody
	public AnnualDTO myAnnPercent(@RequestParam(value="year", required=false) String annYear, Authentication authentication) {
		String empId = authentication.getName();

		if(annYear == null) {
			annYear = String.valueOf(java.time.LocalDate.now().getYear());
		}

		return annService.myAnnual(empId, annYear); 
	}
	
	// 모든 사원 연차 내역
	@GetMapping("/annListAll/{annYear}")
	@ResponseBody
	public Map<String, Object> annListAll(@PathVariable("annYear") String annYear, 
			@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size) {
		Page<AnnualDTO> annPage = annService.getAllAnnByYearPaged(annYear, PageRequest.of(page, size));
		
		Map<String, Object> result = new HashMap<>();
		result.put("totalPages", annPage.getTotalPages());
		result.put("page", page);
		result.put("data", annPage.getContent());
		
		return result;
	}

	
	// 검색창
	@GetMapping("/annSearch")
	@ResponseBody
	public List<AnnualDTO> annSearch(@RequestParam("keyword") String keyword) {
		return annService.searchAnn(keyword);
	}

	// 오늘 연차자 조회
	@GetMapping("/todayAnn") 
	@ResponseBody
	public List<AnnualDTO> todayAnn() {
		return annService.getTodayAnn();
	}


	

}