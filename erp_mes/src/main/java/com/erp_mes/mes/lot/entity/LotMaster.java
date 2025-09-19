package com.erp_mes.mes.lot.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "lot_master")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EntityListeners(AuditingEntityListener.class)
public class LotMaster {
	@Id
	@Column(name = "lot_id", updatable = false, length = 50)
	private String lotId;

// 	각테이블 고유 pk id
	@Column(name = "TARGET_ID", length = 50, nullable = false, updatable = false)
	private String targetId;
	
// 	각테이블 고유 pk id value
	@Column(name = "TARGET_ID_VALUE", length = 50, nullable = false, updatable = false)
	private String targetIdValue;

// 	조회 대상 테이블
	@Column(length = 40, nullable = false, updatable = false)
	private String tableName;

//RM, PR, FG, QA 등
	@Column(length = 20, nullable = false)
	private String type;

	@Column(length = 50)
	private String materialCode;

	@Column(length = 50)
	private String machineId;
	
	//작업지시 테이블 참조
	private Long workOrderId;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "parentLot", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LotMaterialUsage> materialUsagesAsParent = new ArrayList<LotMaterialUsage>();

	@OneToMany(mappedBy = "childLot", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LotMaterialUsage> materialUsagesAsChild = new ArrayList<LotMaterialUsage>();

//	@OneToMany(mappedBy = "lot", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<LotProcessHistory> processHistories = new ArrayList<LotProcessHistory>();

}