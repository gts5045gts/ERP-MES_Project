package com.erp_mes.erp.commonCode.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "COMMON_DT_CODE")
@Getter
@Setter
@ToString(exclude = "comId") 
@EntityListeners(AuditingEntityListener.class)
public class CommonDetailCode {

	@ManyToOne
	@JoinColumn(name = "com_id", referencedColumnName = "COM_ID")
	@JsonBackReference
	private CommonCode comId; // 코드(fk)
	
	@Id
	@Column(name = "com_dt_id")
	private String comDtId; // 상세코드(pk)
	
	@Column(nullable = false, length = 50)
	private String comDtNm; // 코드명
	
	@Column(nullable = false, length = 1)
	private String useYn; // 사용여부
	
	private Integer comDtOrder; // 상세코드정렬순서
	
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt; // 등록일
	
	@LastModifiedDate
	private LocalDateTime updatedAt; // 수정일 
	
}
