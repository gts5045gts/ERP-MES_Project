package com.erp_mes.mes.quality.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InspectionTargetDTO {
    // 공통 필드 (WORK_ORDER, RECEIVING 모두에서 사용)
    private String targetId; // 작업지시 ID 또는 입고 ID
    private String targetName; // 제품명 또는 자재명
    private String lotId; // 로트번호
    private Integer quantity; // 계획 수량 또는 입고 수량

    // 검사 유형 (INSPECTION_FM 테이블에서 가져옴)
    private String inspectionType;      // 검사유형 ID (예: 'QC001')
    private String inspectionTypeName;  // 검사유형 이름 (예: '수입검사')

    // 출처를 구분하기 위한 필드
    private String targetSource; // 'WorkOrder'

    // 검사 등록 시 필요한 추가 필드
    private String productId;   // WORK_ORDER에서 가져옴
    private Long processId;     // WORK_ORDER에서 가져옴
    private String materialId;  // INPUT테이블
    private String empId;       // WORK_ORDER에서 가져옴
    private String processName; 
    private String equipName;  
    private String proSeq;
    private Long goodQty;
    private Long defectQty;
    
    // 상세 조회 모달용
    private String planId;         // 생산 계획 번호
    private String defectReason;   // 불량 상세 사유
    private Long inspectionId; 
}