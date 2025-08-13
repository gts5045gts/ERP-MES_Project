package com.bootstrap.study.approval.entity;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.bootstrap.study.approval.constant.ApprStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "approval")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Appr {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false)
	private Long requestId;
	
	private String empId;
	
	private String reqType;
	
	private String title;
	
	private String content;
	
	private LocalDateTime createAt;
	
	private LocalDateTime updateAt;
	
 	private ApprStatus status = ApprStatus.PROCESSING;
 	
 	private Integer currentStep;
 	
 	private Integer totStep;

}
