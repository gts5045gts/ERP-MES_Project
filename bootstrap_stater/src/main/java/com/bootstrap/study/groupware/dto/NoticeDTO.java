package com.bootstrap.study.groupware.dto;

import java.util.Date;

import com.bootstrap.study.groupware.entity.Notice;
import com.bootstrap.study.personnel.entity.Personnel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoticeDTO {
	
    private Long notId;
    private String empId;
    private String notTitle;
    private String notContent;
    private String notType;
    private Date createAt;
    private Date updateAt;

    // Entity -> DTO 변환 생성자
    public NoticeDTO(Notice notice) {
        this.notId = notice.getNotId();
        this.empId = notice.getEmployee().getEmpId();
        this.notTitle = notice.getNotTitle();
        this.notContent = notice.getNotContent();
        this.notType = notice.getNotType();
        this.createAt = notice.getCreateAt();
        this.updateAt = notice.getUpdateAt();
    }
}
