package com.bootstrap.study.groupware.dto;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;

import com.bootstrap.study.approval.constant.ApprReqType;
import com.bootstrap.study.groupware.entity.Document;
import com.bootstrap.study.personnel.entity.Personnel;

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
    private ApprReqType reqType;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    
//    private Personnel emp;
    
    private String empName;
    private String deptName;
    private String positionName;
    private String doctypename;
    
    @Builder
	public DocumentDTO(Long docId, String empId, String docTitle, String docContent, String docType, ApprReqType reqType, LocalDateTime createAt,
			LocalDateTime updateAt) {
		this.docId = docId;
		this.empId = empId;
		this.docTitle = docTitle;
		this.docContent = docContent;
		this.docType = docType;
		this.reqType = reqType;
		this.createAt = createAt;
		this.updateAt = updateAt;
	}
    
    private static ModelMapper modelMapper = new ModelMapper();
    
    public static DocumentDTO fromEntity(Document document) { return modelMapper.map(document, DocumentDTO.class); }
}
