package com.bootstrap.study.approval.dto;

import com.bootstrap.study.approval.entity.Appr;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

// 모달(상세보기)에 필요한 모든 정보를 담는 DTO
@Getter
@Setter
@NoArgsConstructor
public class ApprFullDTO {

    private Long reqId;
    private String empId;
    private String reqType;
    private String title;
    private String content; // 본문 내용 추가
    private LocalDateTime createAt;
    private String drafterName;
    private String department;
    
    // TODO: 결재선 목록, 휴가 상세 정보 등도 필드로 추가해야 함

    // Entity를 DTO로 변환하는 정적 메소드
    public static ApprFullDTO fromEntity(Appr appr) {
        ApprFullDTO dto = new ApprFullDTO();
        dto.setReqId(appr.getReqId());
        dto.setEmpId(appr.getEmpId());
        dto.setReqType(appr.getReqType());
        dto.setTitle(appr.getTitle());
        dto.setContent(appr.getContent());
        dto.setCreateAt(appr.getCreateAt());
        // TODO: 직원 이름, 부서명 등 실제 데이터 채우기
        dto.setDrafterName("임시기안자");
        dto.setDepartment("임시부서");
        return dto;
    }
}