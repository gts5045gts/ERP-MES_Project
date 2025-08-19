package com.bootstrap.study.approval.entity;

import java.time.LocalDate;

import com.bootstrap.study.approval.constant.ApprHalfType;
import com.bootstrap.study.approval.constant.ApprVacType;

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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "approval_detail")
@Getter
@Setter
public class ApprDetail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "det_id", updatable = false)
	private Long id;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private	LocalDate startDate;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private	LocalDate endDate;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private	ApprVacType vacType;
	
	@Enumerated(EnumType.STRING)
	private	ApprHalfType halfType;
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "req_id")
	private	Appr appr;

}
