package com.erp_mes.mes.pm.dto;

import lombok.Data;

@Data
public class WorkOrderDTO {
    private String workOrderId;
    private String processId;
    private String empId;
    private String workOrderStatus;
    private String lotId; 
    private Long quantity;

    // JOIN을 통해 가져올 필드
    private String productName;
    private String empName;
    private String processName;
}
