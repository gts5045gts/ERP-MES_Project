package com.erp_mes.mes.lot.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "lot_process_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotProcessHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "history_id")
	private Long historyId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "lot_id", referencedColumnName = "lot_id", nullable = false)
	private LotMaster lot;

	@Column(name = "process_code", nullable = false, length = 20)
	private String processCode;

	@Column(name = "machine_id", length = 100)
	private String machineId;

	@Column(name = "operator", length = 100)
	private String operator;

	@Column(name = "process_start")
	private LocalDateTime processStart;

	@Column(name = "process_end")
	private LocalDateTime processEnd;

	@Column(name = "input_qty")
	private Integer inputQty;

	@Column(name = "result_qty")
	private Integer resultQty;

	@Column(name = "scrap_qty")
	private Integer scrapQty;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}