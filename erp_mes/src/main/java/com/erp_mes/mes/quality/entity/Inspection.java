package com.erp_mes.mes.quality.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "Inspection")
@Getter
@Setter
@NoArgsConstructor
public class Inspection {

 @Id
 @Column(name = "INSPECTION_ID")
 private Long inspectionId; // 검사ID

 @Column(name = "INSPECTION_TYPE")
 private String inspectionType; // 검사유형

 @Column(name = "PRODUCT_ID")
 private Long productId; // 제품ID

 @Column(name = "PROCESS_ID")
 private Long processId; // 공정ID

 @Column(name = "INSPECTION_DATE")
 private LocalDateTime inspectionDate; // 검사일자

 @Column(name = "INSPECTION_NAME")
 private String inspectionName; // 검사자명

 @Column(name = "LOT_ID")
 private String lot_id; // 로트번호
 
}