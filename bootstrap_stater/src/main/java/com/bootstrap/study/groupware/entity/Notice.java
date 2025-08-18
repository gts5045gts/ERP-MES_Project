package com.bootstrap.study.groupware.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "NOTICE")
@Getter
@Setter
@NoArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOT_ID")
    private Long notId;

    @Column(name = "EMP_ID")
    private Long empId;

    @Column(name = "NOT_TITLE", length = 255)
    private String notTitle;

    @Lob
    @Column(name = "NOT_CONTENT")
    private String notContent;

    @Column(name = "NOT_TYPE", length = 100)
    private String notType;

    @Column(name = "CREATE_AT")
    private Date createAt;

    @Column(name = "UPDATE_AT")
    private Date updateAt;

}
