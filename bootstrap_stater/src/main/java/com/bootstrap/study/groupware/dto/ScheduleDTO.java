package com.bootstrap.study.groupware.dto;

import java.time.LocalDateTime;
import java.util.Date;

import com.bootstrap.study.groupware.entity.Schedule;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleDTO {
    private Long schId;
    private Long empId;
    private String schTitle;
    private String schContent;
    private String schType;
    private LocalDateTime starttimeAt;
    private LocalDateTime endtimeAt;
    private Date createAt;
    private Date updateAt;

    // ⭐ Entity -> DTO 변환 생성자
    public ScheduleDTO(Schedule schedule) {
        this.schId = schedule.getSchId();
        this.empId = schedule.getEmpId();
        this.schTitle = schedule.getSchTitle();
        this.schContent = schedule.getSchContent();
        this.schType = schedule.getSchType();
        this.starttimeAt = schedule.getStarttimeAt();
        this.endtimeAt = schedule.getEndtimeAt();
        this.createAt = schedule.getCreateAt();
        this.updateAt = schedule.getUpdateAt();
    }
}