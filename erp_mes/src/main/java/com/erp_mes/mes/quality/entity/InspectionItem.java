package com.erp_mes.mes.quality.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq_gen")
    @SequenceGenerator(
        name = "item_seq_gen",
        sequenceName = "INSPECTION_ITEM_SEQ", 
        allocationSize = 1
    )
    private Long itemId;

    @Column(name = "PRO_ID", nullable = true) 
    private Long proId; 
    
    @Column(name = "MATERIAL_ID", nullable = true)
    private String materialId;
    
    @Column(name = "INSPECTION_FM_ID")
    private Long inspectionFMId;

    @Column(name = "TOLERANCE_VALUE")
    private BigDecimal toleranceValue;

    @Column(name = "UNIT")
    private String unit;
}