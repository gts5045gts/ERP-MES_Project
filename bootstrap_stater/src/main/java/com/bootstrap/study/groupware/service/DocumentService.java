package com.bootstrap.study.groupware.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bootstrap.study.groupware.dto.DocumentDTO;
import com.bootstrap.study.groupware.entity.Document;
import com.bootstrap.study.groupware.mapper.DocumentMapper;
import com.bootstrap.study.groupware.repository.DocumentRepository;
import com.bootstrap.study.personnel.dto.PersonnelDTO;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
@Log4j2
@Service
public class DocumentService {
	
	private final DocumentRepository documentRepository;
	private final DocumentMapper documentMapper;

	public List<DocumentDTO> getAllDocuments() {
		
		return documentMapper.findAllDocuments();
		
 		// Personnel 엔티티 목록을 가져와서 DTO로 변환
// 		List<Document> doclList = documentRepository.findAll();
// 		return doclList.stream()
// 				.map(DocumentDTO::fromEntity)
// 				.collect(Collectors.toList());
		
	}

	public DocumentDTO getDocument(Long docId) {
		
		// 상품 1개 정보(= 1개 엔티티) 조회
		Document document = documentRepository.findById(docId)
				.orElseThrow(() -> new EntityNotFoundException("해당 문서가 존재하지 않습니다!"));
		
		// Item -> ItemDTO 변환
		DocumentDTO documentDTO = DocumentDTO.fromEntity(document);
		
		return documentDTO;
	}

	@Transactional
    public void updateDocument(DocumentDTO dto) {
        Document document = documentRepository.findById(dto.getDocId())
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + dto.getDocId()));

        document.updateFromDto(dto);
        // 변경 감지 후 트랜잭션 커밋 시점에 DB 반영됨
    }
}
