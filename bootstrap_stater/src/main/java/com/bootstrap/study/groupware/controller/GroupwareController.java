package com.bootstrap.study.groupware.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bootstrap.study.commonCode.service.CommonCodeService;
import com.bootstrap.study.groupware.dto.DocumentDTO;
import com.bootstrap.study.groupware.entity.Document;
import com.bootstrap.study.groupware.repository.DocumentRepository;
import com.bootstrap.study.personnel.dto.PersonnelLoginDTO;
import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.repository.PersonnelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/groupware")
@Log4j2
@RequiredArgsConstructor
public class GroupwareController {
	
	private final CommonCodeService comService;
	
	@Autowired
	private PersonnelRepository personnelRepository;
	@Autowired
	private DocumentRepository documentRepository;
	
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

	//공통 문서 작성
	@PostMapping("/save")
	public String saveDoc(DocumentDTO documentDTO) {
		// 1. 현재 로그인한 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		String loginEmpId = authentication.getName();

		Document doc = new Document();
		
		log.info("asdfffffaasdfasdf>>>>>>>>>>>>>>>>>>>>>>>"+documentDTO.getEmpId());

		doc.setDocTitle(documentDTO.getDocTitle());
		doc.setDocContent(documentDTO.getDocContent());
		doc.setEmpId(documentDTO.getEmpId());
		doc.setDocType(documentDTO.getDocType());
		doc.setCreateAt(new Date());
		doc.setUpdateAt(new Date());

//		documentRepository.save(doc);
		
		return null;

//		return "redirect:/document";
	}
}
