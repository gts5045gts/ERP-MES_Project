package com.bootstrap.study.commonCode.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "COMMON_CODE")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class CommonCode {

	@Id
	private String comId; // 코드
	
	@Column(nullable = false, length = 50)
	private String comNm; // 코드명
	

	@Column(nullable = false, length = 1)
	private String useYn; // 사용여부
	
	private Integer comOrder; // 정렬순서
	
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt; // 등록일
	
	@LastModifiedDate
	private LocalDateTime updatedAt; // 수정일 
}
