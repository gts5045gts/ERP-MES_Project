package com.bootstrap.study.groupware.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleDTO {
    private String title;
    private String content;
    private Date startDate;
    private Date endDate;
    private String schType;
}