package com.bootstrap.study.groupware.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bootstrap.study.commonCode.repository.CommonDetailCodeRepository;
import com.bootstrap.study.groupware.dto.DocumentDTO;
import com.bootstrap.study.groupware.entity.Document;
import com.bootstrap.study.groupware.repository.DocumentRepository;
import com.bootstrap.study.personnel.dto.PersonnelDTO;
import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.repository.PersonnelRepository;
import com.bootstrap.study.personnel.service.PersonnelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
@Log4j2
@Service
public class DocumentService {
	
	private final DocumentRepository documentRepository;

	public List<DocumentDTO> getAllDocuments() {
		
 		// Personnel 엔티티 목록을 가져와서 DTO로 변환
 		List<Document> doclList = documentRepository.findAll();
 		return doclList.stream()
 				.map(DocumentDTO::fromEntity)
 				.collect(Collectors.toList());
		
	}
	
}
