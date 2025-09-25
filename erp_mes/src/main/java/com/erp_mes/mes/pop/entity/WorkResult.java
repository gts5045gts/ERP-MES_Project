package com.erp_mes.mes.pop.entity;


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
@Table(name = "WORK_RESULT")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class WorkResult {
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "result_id")
	private Long resultId;		// 실적 id
	
	@Column(name = "work_order_id", nullable = false)
	private Long workOrderId; 		// 작업지시 id (fk)
	
	@Column(nullable = false)
	private Long goodQty = 0L;			// 생산수량
	
	@Column
	private Long defectItemId;		// 항목 ID(불량) (fk)
	
	@Column
	private Long defectQty = 0L;			// 불량수량

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt; // 등록시간
	
	@LastModifiedDate
	private LocalDateTime updatedAt; // 수정시간
	
	@Column(name = "LOT_ID")   // 로트
	private String lotId;
	
	@Column(name = "in_id")
    private String inId;		// 입고(fk)
	
	@Column(name = "route_id")
    private Long routeId;		// 라우팅
	

}
