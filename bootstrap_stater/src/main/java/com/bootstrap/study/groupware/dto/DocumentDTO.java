package com.bootstrap.study.groupware.dto;

import java.util.Date;

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
    private Date createAt;
    private Date updateAt;
    
    @Builder
	public DocumentDTO(Long docId, String empId, String docTitle, String docContent, String docType, Date createAt,
			Date updateAt) {
		this.docId = docId;
		this.empId = empId;
		this.docTitle = docTitle;
		this.docContent = docContent;
		this.docType = docType;
		this.createAt = createAt;
		this.updateAt = updateAt;
	}
}
