package com.bootstrap.study.groupware.dto;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;

import com.bootstrap.study.groupware.entity.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DocumentDTO {
	
	private Long docId;
    private String empId;
    private String docTitle;
    private String docContent;
    private String docType;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    
    @Builder
	public DocumentDTO(Long docId, String empId, String docTitle, String docContent, String docType, LocalDateTime createAt,
			LocalDateTime updateAt) {
		this.docId = docId;
		this.empId = empId;
		this.docTitle = docTitle;
		this.docContent = docContent;
		this.docType = docType;
		this.createAt = createAt;
		this.updateAt = updateAt;
	}
    
    private static ModelMapper modelMapper = new ModelMapper();
    
    public static DocumentDTO fromEntity(Document document) { return modelMapper.map(document, DocumentDTO.class); }
}
