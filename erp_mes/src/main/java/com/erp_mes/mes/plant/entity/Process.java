package com.erp_mes.mes.plant.entity;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Table(name = "process")
public class Process {
	
	@Id
    @Column(name = "process_id")
    private String processId; // 단순 PK

    @ManyToOne
    @JoinColumn(name = "common_dt_id", referencedColumnName = "com_dt_id") // FK
    private CommonDetailCode common;
	
	@Column(nullable = false, name = "user_yn")
	private String useYn;
	
	@Column(nullable = false, name = "inspection_yn")
	private String insYn;
	
	@Column(nullable = false, name = "note")
	private String note;

}
