package com.erp_mes.mes.quality.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inspection_seq_gen")
	@SequenceGenerator(name = "inspection_seq_gen", sequenceName = "INSPECTION_SEQ", allocationSize = 1)
	@Column(name = "INSPECTION_ID")
	private Long inspectionId;

	@Column(name = "INSPECTION_TYPE")
	private String inspectionType; // 검사유형

	@Column(name = "PRODUCT_ID")
	private String productId; // 제품ID

	@Column(name = "PROCESS_ID")
	private Long processId; // 공정ID
	
	@Column(name = "MATERAIL_ID")
	private String materailId;

	@Column(name = "INSPECTION_DATE")
	private LocalDateTime inspectionDate; // 검사일자

	@Column(name = "EMP_ID")
	private String empId; // 검사자명

	@Column(name = "LOT_ID")
	private String lot_id; // 로트번호

}