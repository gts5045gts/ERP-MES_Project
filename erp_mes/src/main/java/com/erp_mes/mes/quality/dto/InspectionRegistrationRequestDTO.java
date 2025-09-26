package com.erp_mes.mes.quality.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InspectionRegistrationRequestDTO {
	
    // 기존 필드
    private String targetSource;
    private String lotId;
    private String inspectionType; // QC001, QC002 등
    private String empId;
    private String remarks;
    
    // 검사 대상 고유 ID (WORK_ORDER_ID 또는 IN_ID)
    private String targetId;
    
    // INSPECTION 테이블에 등록할 정보
    private String productId;
    private Long processId;
    private String materialId;
    
    // 공정 검사 수량 등록을 위한 필드
    private Long acceptedCount; // 합격 수량
    private Long defectiveCount; // 불량 수량
    private String defectType; // 불량 사유 (코드)
    private String proSeq; // 공정 순서 (필요시 사용)

    // (기존) 검사 항목별 실측값 및 결과 (수량 검사 시에는 사용되지 않음)
    private List<InspectionResultDataDTO> inspectionResults;
}