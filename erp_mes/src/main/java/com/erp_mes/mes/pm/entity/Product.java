package com.erp_mes.mes.pm.entity;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "PRODUCT")

@Getter
@Setter
@ToString
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {

	// 제품코드
	@Id
	@Column(name = "product_id", length = 20)
	private String productId; // pk
	
	// 제품명
	@Column(nullable = false, name = "product_name", length = 100)
	private String productName;
	
	// 제품구분
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "product_type", referencedColumnName = "com_dt_id")
	private String productType;
	
	// 단위
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "unit", referencedColumnName = "com_dt_id")
	private String unit;
	
	// 등록일
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	// 수정일
	@Column(name = "upadated_at")
	private LocalDateTime updatedAt;
	
	// 규격
	@Column(name = "spec")
	private String spec;
	
	
}



















