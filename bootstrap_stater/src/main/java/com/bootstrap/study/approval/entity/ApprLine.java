package com.bootstrap.study.approval.entity;

import com.bootstrap.study.approval.constant.ApprDecision;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_line")
@Getter
@Setter
public class ApprLine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "approval_line_seq_generator")
    @SequenceGenerator(name="approval_line_seq_generator", sequenceName="approval_line_seq", allocationSize=1)
    @Column(name = "line_id", updatable = false)
    private	Long id;

    @Column(nullable = false)
    @ColumnDefault("0")
    private	int	stepNo; // 결제 순번

    @Column(nullable = false, length = 20)
    private	String apprId; //결제자 id

    @Enumerated(EnumType.STRING)
    private ApprDecision decision; //승인 반려 상태

    @LastModifiedDate
    private LocalDateTime decDate;

    @Column(length = 500)
    private	String	comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "req_id", nullable = false)
    private Appr appr;
}
