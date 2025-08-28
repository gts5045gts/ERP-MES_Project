package com.bootstrap.study.groupware.entity;

import java.util.Date;

import com.bootstrap.study.personnel.entity.Personnel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "SHARED_DOCUMENT")
@Getter
@Setter
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "DOC_ID")
    private Long docId;

    @Column(nullable = false, length = 20)
	private String empId;

    @Column(nullable = false, name = "DOC_TITLE", length = 100)
    private String docTitle;

    @Lob
    @Column(name = "DOC_CONTENT")
    private String docContent;

    @Column(nullable = false, name = "DOC_TYPE", length = 100)
    private String docType;

    @Column(nullable = false, name = "CREATE_AT")
    private Date createAt;

    @Column(name = "UPDATE_AT")
    private Date updateAt;

}
