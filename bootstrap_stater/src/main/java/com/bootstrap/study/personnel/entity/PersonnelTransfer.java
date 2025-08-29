package com.bootstrap.study.personnel.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "transfer")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PersonnelTransfer {
	@Id
    @Column(name = "req_id", updatable = false)
    private Long reqId; // 전자결재 문서 ID (FK: approval 테이블)

    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId; // 발령 대상 사원

    @Column(name = "transfer_type", nullable = false, length = 20)
    private String transferType; // 발령 구분 (승진, 부서이동)

    @Column(name = "old_dept", nullable = false, length = 50)
    private String oldDept; // 기존 부서

    @Column(name = "new_dept", nullable = false, length = 50)
    private String newDept; // 신규 부서

    @Column(name = "old_position", nullable = false, length = 50)
    private String oldPosition; // 기존 직급

    @Column(name = "new_position", nullable = false, length = 50)
    private String newPosition; // 신규 직급
    
    @UpdateTimestamp
	@Column(nullable = false, name = "update_at")
	private Timestamp update;
}
