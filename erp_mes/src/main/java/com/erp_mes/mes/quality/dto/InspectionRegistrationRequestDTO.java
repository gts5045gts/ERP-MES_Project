package com.erp_mes.mes.quality.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InspectionRegistrationRequestDTO {
    private String targetSource; 
    private String lotId;
    private String inspectionType; // QC001, QC002 등
    private String empId;
    private String remarks;
    
    // 검사 대상 고유 ID
    private String targetId; 
    
    // INSPECTION 테이블에 등록할 정보
    private String productId;
    private Long processId;
    private String materialId;
    
    // 검사 항목별 실측값 및 결과
    private List<InspectionResultDataDTO> inspectionResults;
}