package com.bootstrap.study.personnel.dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PersonnelTransferDTO {
	
	private Long reqId; // 전자결재 문서 ID (FK)
    private String empId; // 발령 대상 사원 ID
    private String transferType; // 발령 구분
    private String oldDept; // 기존 부서
    private String newDept; // 신규 부서
    private String oldPosition; // 기존 직급
    private String newPosition; // 신규 직급
    private Timestamp create;	// 발령일
    private Timestamp update;	// 수정일
    
}
