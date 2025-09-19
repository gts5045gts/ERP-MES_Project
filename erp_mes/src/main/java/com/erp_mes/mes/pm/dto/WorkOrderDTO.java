package com.erp_mes.mes.pm.dto;

import lombok.Data;

@Data
public class WorkOrderDTO {
    private String workOrderId;
    private String processId;
    private String empId;
    private String workOrderStatus;
    private String lotId; 

    // JOIN을 통해 가져올 필드 - 품질관리
    private Long planQuantity;
    private String productId;
    private String productName;
    private String processName;
    private String empName;
    private String bomId;
    private String inspectionType;
    private String inspectionTypeName;
    
}
