package com.erp_mes.mes.quality.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionResultDTO {
    private Long resultId;
    private Long inspectionId;
    private String inspectionType;
    private String result;
    private String remarks;
    
    // Inspection 테이블의 정보
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inspectionDate;
    private String empId;
    private String lotId;
    private Long workOrderId;
    
    // JOIN을 통해 추가된 이름 정보 필드 (쿼리 결과와 일치)
    private String inspectionTypeName;
    private String empName;
    private String materialName; // material 테이블에서 직접 가져오도록
    private String productName;  // product 테이블에서 직접 가져오도록
    private String processName;  // process 테이블에서 직접 가져오도록

    // 화면 표기를 위한 필드를 명확하게 분리
    private String displayTargetName; // 수입검사 시 자재명, 공정/포장검사 시 제품명
    private String displayProcessName; // 공정/포장검사 시 공정명
}