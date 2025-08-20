package com.bootstrap.study.groupware.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SCHEDULE")
@Getter
@Setter
@NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCH_ID")
    private Long schId;

    @Column(name = "EMP_ID")
    private Long empId;
    
    @Column(name = "SCH_TITLE")
    private String schTitle;
    
    @Column(name = "SCH_CONTENT")
    private String schContent;
    
    @Column(name = "SCH_TYPE")
    private String schType;
    
    @Column(name = "STARTTIME_AT")
    private Date starttimeAt;
    
    @Column(name = "ENDTIME_AT")
    private Date endtimeAt;
    
    @CreationTimestamp
    @Column(name = "CREATE_AT")
    private Date createAt;
    
    @UpdateTimestamp
    @Column(name = "UPDATE_AT")
    private Date updateAt;
}
