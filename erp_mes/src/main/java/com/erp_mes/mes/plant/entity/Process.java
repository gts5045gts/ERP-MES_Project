package com.erp_mes.mes.plant.entity;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity
@Table(name = "process")
public class Process {
	
	@Id
	private String proId;
	
	@Column(nullable = false, name = "PRO_NM")
	private String proNm;
	

	@JoinColumn(nullable = false, name = "PRO_TYPE", referencedColumnName = "COM_DT_ID")
	private CommonDetailCode common;
	
	@Column(nullable = false, name = "PRO_NM")
	private String note;
	
	

}
