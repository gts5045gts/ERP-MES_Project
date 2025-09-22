package com.erp_mes.mes.lot.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lot_material_usage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotMaterialUsage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "usage_id")
	private Long usageId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "parent_lot_id", referencedColumnName = "lot_id", nullable = false)
	private LotMaster parentLot;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "child_lot_id", referencedColumnName = "lot_id", nullable = false)
	private LotMaster childLot;

	@Column(name = "qty_used", nullable = false)
	private Integer qtyUsed;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}
