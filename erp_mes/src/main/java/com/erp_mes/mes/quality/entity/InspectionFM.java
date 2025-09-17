package com.erp_mes.mes.quality.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "INSPECTION_FM")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionFM {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "insp_fm_seq_gen")
    @SequenceGenerator(
        name = "insp_fm_seq_gen",
        sequenceName = "INSPECTION_FM_SEQ",
        allocationSize = 1
    )
    @Column(name = "INSPECTION_FM_ID")
    private Long inspectionFMId;

    @Column(name = "INSPECTION_TYPE")
    private String inspectionType;

    @Column(name = "ITEM_NAME")
    private String itemName;

    @Column(name = "METHOD_NAME")
    private String methodName;
}