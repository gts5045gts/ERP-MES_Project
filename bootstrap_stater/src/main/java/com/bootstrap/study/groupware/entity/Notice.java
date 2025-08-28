package com.bootstrap.study.groupware.entity;





import java.util.Date;

import com.bootstrap.study.personnel.entity.Personnel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "NOTICE")
@Getter
@Setter
@NoArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "NOT_ID")
    private Long notId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID", referencedColumnName = "EMP_ID")
    private Personnel employee;

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
