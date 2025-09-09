package com.erp_mes.erp.personnel.dto;

import java.sql.Timestamp;
import java.time.LocalDate;

import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.erp.personnel.entity.PersonnelTransfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelTransferDTO {
	
	private Long reqId; // 전자결재 문서 ID (FK)
    private String empId; // 발령 대상 사원 ID
    private String name; // 발령 대상 이름
    private String transferType; // 발령 구분
    private String reason; // 발령 사유
    private LocalDate transDate; // 발령일 
    private Timestamp create;	// 발령일
    private Timestamp update;	// 수정일
    
    private String oldDeptId;
    private String oldDeptName; // 기존 부서명
    
    private String newDeptId;
    private String newDeptName; // 신규 부서명

    private String oldPosId;
    private String oldPosName; // 기존 직급명
    
    private String newPosId;
    private String newPosName; // 신규 직급명
    
 // Entity -> DTO 변환을 위한 정적 팩토리 메서드
    public static PersonnelTransferDTO fromEntity(PersonnelTransfer transfer, CommonDetailCodeRepository comDetailRepo) {
    	
    	PersonnelTransferDTO dto = new PersonnelTransferDTO();
    	
    	dto.setEmpId(transfer.getEmpId());
        dto.setName(transfer.getName());
        dto.setTransferType(transfer.getTransferType());
        dto.setTransDate(transfer.getTransDate());
        
     // 코드-이름 변환 로직
        comDetailRepo.findByComDtId(transfer.getOldDept()).ifPresent(code -> {
            dto.setOldDeptId(code.getComDtId());
            dto.setOldDeptName(code.getComDtNm());
        });
        comDetailRepo.findByComDtId(transfer.getNewDept()).ifPresent(code -> {
            dto.setNewDeptId(code.getComDtId());
            dto.setNewDeptName(code.getComDtNm());
        });
        comDetailRepo.findByComDtId(transfer.getOldPosition()).ifPresent(code -> {
            dto.setOldPosId(code.getComDtId());
            dto.setOldPosName(code.getComDtNm());
        });
        comDetailRepo.findByComDtId(transfer.getNewPosition()).ifPresent(code -> {
            dto.setNewPosId(code.getComDtId());
            dto.setNewPosName(code.getComDtNm());
        });
        
        return dto;
	}
    
}
