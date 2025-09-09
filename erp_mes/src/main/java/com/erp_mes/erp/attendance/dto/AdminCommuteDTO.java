package com.erp_mes.erp.attendance.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AdminCommuteDTO {
    private String empId;
    private String empName;
    private String deptName;
    private String posName;
    private String workStatus;
    private String workStatusNm;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String commuteDate;

}
