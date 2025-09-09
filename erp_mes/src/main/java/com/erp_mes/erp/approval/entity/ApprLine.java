package com.erp_mes.erp.approval.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.LastModifiedDate;

import com.erp_mes.erp.approval.constant.ApprDecision;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "approval_line")
@Getter
@Setter
@NoArgsConstructor
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
