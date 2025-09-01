package com.bootstrap.study.groupware.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bootstrap.study.approval.constant.ApprReqType;
import com.bootstrap.study.commonCode.service.CommonCodeService;
import com.bootstrap.study.groupware.dto.DocumentDTO;
import com.bootstrap.study.groupware.entity.Document;
import com.bootstrap.study.groupware.repository.DocumentRepository;
import com.bootstrap.study.groupware.service.DocumentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Controller
@RequestMapping("/groupware")
@Log4j2
@RequiredArgsConstructor
public class GroupwareController {
	
	private final CommonCodeService comService;
	
	@Autowired
	private DocumentRepository documentRepository;
	
	private final DocumentService documentService;
	
	//공통문서목록
	@GetMapping("/document")
	public String document(Model model) {
		
		List<DocumentDTO> documents = documentService.getAllDocuments();
		model.addAttribute("documents", documents);
		
		return "/gw/document";
	}
	
	//공통 문서 작성
	@GetMapping("/docWrite")
	public String docWrite(Model model) {
		
		model.addAttribute("dtCodes", comService.findByComId("DOC"));
		model.addAttribute("documentDTO", new DocumentDTO());
		
		return "/gw/docWrite";
	}

	//공통 문서 저장
	@PostMapping("/save")
	public String saveDoc(DocumentDTO documentDTO) {
		// 1. 현재 로그인한 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		String loginEmpId = authentication.getName();

		Document doc = new Document();
		
		doc.setDocTitle(documentDTO.getDocTitle());
		doc.setDocContent(documentDTO.getDocContent());
		doc.setEmpId(loginEmpId);
		doc.setDocType(documentDTO.getDocType());
		doc.setReqType(documentDTO.getReqType());

		documentRepository.save(doc);
		
		return "redirect:/groupware/document";
	}
	
	//공통문서 상세페이지
	@GetMapping("/docView/{docId}")
	public String docView(@PathVariable("docId") Long docId, Model model) {
		
		DocumentDTO documentDTO = documentService.getDocument(docId);
		
		model.addAttribute("dtCodes", comService.findByComId("DOC"));
		model.addAttribute("documentDTO", documentDTO);
		
		return "/gw/docView";
	}
}
