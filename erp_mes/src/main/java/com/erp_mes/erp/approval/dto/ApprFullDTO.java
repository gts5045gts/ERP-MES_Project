package com.erp_mes.erp.approval.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

import com.erp_mes.erp.approval.entity.Appr;

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
    
	// 결재선 정보 추가
	private List<ApprLineInfo> approvalLines;
	
	// 결재선 정보를 담을 내부 클래스
	@Getter
	@Setter
	@NoArgsConstructor
	public static class ApprLineInfo {
	    private Integer stepNo;
	    private String apprId;
	    private String apprName;
	    private String decision;
	    private LocalDateTime decDate;
	    private String comments;
	}
}