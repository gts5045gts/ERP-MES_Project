package com.erp_mes.mes.pop.dto;

import java.time.LocalDateTime;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DefectDTO {
	
    private Long defectItemId;
    private String defectType;
    private String defectReason;
    private Long defectQty;
    private String productName; // 불량품명
    private String empId; // 작업자ID
    private Integer defectLocation; // 불량위치 1:pop 2:QC/QA
    private LocalDateTime defectDate;
    private Long workOrderId;
    private String lotId;
	
	

}
