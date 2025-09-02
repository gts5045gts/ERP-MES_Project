package com.bootstrap.study.personnel.dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
//    private String oldDept; // 기존 부서 ID
//    private String newDept; // 신규 부서 ID
//    private String oldPosition; // 기존 직급 ID
//    private String newPosition; // 신규 직급 ID
    private LocalDateTime transDate; // 발령일 
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
    
    
 // ✅ Map에서 DTO로 변환하는 정적 팩토리 메서드
    public static PersonnelTransferDTO fromMap(Map<String, String> map) {
        // 날짜 파싱
        LocalDate transDate = null;
        if (map.containsKey("transDate")) {
            transDate = LocalDate.parse(map.get("transDate"), DateTimeFormatter.ISO_LOCAL_DATE);
        }
        
        return PersonnelTransferDTO.builder()
            .empId(map.get("empId"))
            .oldDeptId(map.get("oldDeptId"))
            .newDeptId(map.get("newDeptId"))
            .oldPosId(map.get("oldPosId"))
            .newPosId(map.get("newPosId"))
            .transDate(transDate.atStartOfDay()) // LocalDate를 LocalDateTime으로 변환
            .build();
    }
}
