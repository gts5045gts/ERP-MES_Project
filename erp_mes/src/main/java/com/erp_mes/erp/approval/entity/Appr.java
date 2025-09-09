package com.erp_mes.erp.approval.entity;

import java.time.LocalDate;
import java.time.LocalDateTime; 
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.erp_mes.erp.approval.constant.ApprStatus;

@Entity
@Table(name = "approval")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
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
	
	@Column(nullable = false)
	private LocalDate requestAt;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createAt;

	@LastModifiedBy
	private LocalDateTime updateAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
 	private ApprStatus status = ApprStatus.REQUESTED;
 	
	@Column(name = "TOT_STEP")
 	private int totStep;

 	@OneToMany(mappedBy = "appr", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApprLine> appLines = new ArrayList<ApprLine>();
 	
 	 // 새로 추가하는 ApprDetail 연결
    @OneToMany(mappedBy = "appr", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApprDetail> apprDetails = new ArrayList<ApprDetail>();

	public Appr(String empId, String reqType, String title, String content, int totStep) {
		this.empId = empId;
		this.reqType = reqType;
		this.title = title;
		this.content = content;
		this.totStep = totStep;
	}
	
	public void addLine(ApprLine line) {
	    this.appLines.add(line);
	    line.setAppr(this);
	}
	
	 // 연관관계 편의 메서드
    public void addDetail(ApprDetail detail) {
        this.apprDetails.add(detail);
        detail.setAppr(this);
    }
    
    @Transient // DB에는 저장 안됨
    private boolean hasRejection;  
}
