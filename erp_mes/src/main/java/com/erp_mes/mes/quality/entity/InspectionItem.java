package com.erp_mes.mes.quality.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "INSPECTION_ITEM")
@Getter
@Setter
public class InspectionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ITEM_ID")
    private Long itemId;

    @Column(name = "INSPECTION_TYPE")
    private String inspectionType;

    @Column(name = "ITEM_NAME")
    private String itemName;

    @Column(name = "TOLERANCE_VALUE")
    private Double toleranceValue;

    @Column(name = "UNIT")
    private String unit;
}