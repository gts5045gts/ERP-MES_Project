package com.erp_mes.mes.quality.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class InspectionDTO {

    private Long inspectionId;
    private String inspectionType;
    private String productId;
    private String materialId;
    private Long processId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inspectionDate;
    private String empId;
    private String lotId;
}
