package com.erp_mes.mes.quality.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "INSPECTION_ITEM")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InspectionItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ITEM_ID")
	private Long itemId;

	@Column(name = "PRODUCT_CODE")
	private String productCode;

	@Column(name = "INSPECTION_FM_ID")
	private Long inspectionFMId;

	@Column(name = "TOLERANCE_VALUE")
	private Double toleranceValue;

	@Column(name = "UNIT")
	private String unit;
}