package com.erp_mes.erp.commonCode.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	@Column(name = "com_id")
	private String comId; // 코드
	
	@Column(nullable = false, length = 50)
	private String comNm; // 코드명
	

	@Column(nullable = false, length = 1)
	private String useYn; // 사용여부
	
	
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt; // 등록일
	
	@LastModifiedDate
	private LocalDateTime updatedAt; // 수정일 
	
	@OneToMany(mappedBy = "comId")
	@JsonManagedReference
    private List<CommonDetailCode> detailCodes = new ArrayList<>();
	
}
