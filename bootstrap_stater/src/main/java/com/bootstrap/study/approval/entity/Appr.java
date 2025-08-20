package com.bootstrap.study.approval.entity;

import java.time.LocalDateTime; 
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.bootstrap.study.approval.constant.ApprStatus;

@Entity
@Table(name = "approval")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Appr {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "approval_seq_generator")
    @SequenceGenerator(name="approval_seq_generator", sequenceName="approval_seq", allocationSize=1)
    @Column(updatable = false)
	private Long reqId;

	@Column(nullable = false, length = 20)
	private String empId;

	@Column(length = 30)
	private String reqType;

	@Column(length = 200, nullable = false)
	private String title;

	@Column(length = 4000)
	private String content;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createAt;

	@LastModifiedBy
	private LocalDateTime updateAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
 	private ApprStatus status;
 	
 	private int currentStep;
 	
 	private int totStep;

 	@OneToMany(mappedBy = "appr", fetch = FetchType.LAZY)
    private  List<ApprLine> appLines = new ArrayList<ApprLine>();
 	
 	 // 새로 추가하는 ApprDetail 연결
    @OneToMany(mappedBy = "appr", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApprDetail> apprDetails = new ArrayList<>();

	public Appr(String empId, String reqType, String title, String content, int currentStep, int totStep) {
		this.empId = empId;
		this.reqType = reqType;
		this.title = title;
		this.content = content;
		this.currentStep = currentStep;
		this.totStep = totStep;
	}
}
