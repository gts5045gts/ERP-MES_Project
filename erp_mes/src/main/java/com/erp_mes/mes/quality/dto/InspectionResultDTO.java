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
    private String itemId;
    private String result;
    private String remarks;
    
    // Inspection 테이블의 정보
    private String inspectionType;
    private String productId;
    private Long processId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inspectionDate;
    private String empId;
    private String lotId;
    
    // JOIN을 통해 추가된 이름 정보 필드
    private String inspectionTypeName;
    private String productName;
    private String processName;
    private String empName;
    private Long workOrderId;
}