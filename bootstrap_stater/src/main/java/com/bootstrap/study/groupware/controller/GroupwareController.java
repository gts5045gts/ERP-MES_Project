package com.bootstrap.study.groupware.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bootstrap.study.commonCode.dto.CommonCodeDTO;
import com.bootstrap.study.commonCode.dto.CommonDetailCodeDTO;
import com.bootstrap.study.commonCode.entity.CommonDetailCode;
import com.bootstrap.study.commonCode.service.CommonCodeService;
import com.bootstrap.study.groupware.dto.DocumentDTO;
import com.bootstrap.study.groupware.dto.NoticeDTO;
import com.bootstrap.study.personnel.controller.PersonnelController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/groupware")
@Log4j2
@RequiredArgsConstructor
public class GroupwareController {
	
	private final CommonCodeService comService;
	
//	@GetMapping("/notice")
//	public String notice() {
//		log.info("GroupwareController gwMain()");
//		
//		return "/gw/notice";
//	}
	
//	@GetMapping("/schedule")
//	public String schedule() {
//		log.info("GroupwareController aSchedule()");
//		
//		return "/gw/schedule";
//	}
	
	@GetMapping("/document")
	public String document() {
		log.info("GroupwareController document()");
		
		return "/gw/document";
	}
	
	@GetMapping("/docWrite")
	public String docWrite(Model model) {
//		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+codeDetail);
		model.addAttribute("dtCodes", comService.findByComId("DOC"));
		model.addAttribute("documentDTO", new DocumentDTO());
		
		return "/gw/docWrite";
	}
}
