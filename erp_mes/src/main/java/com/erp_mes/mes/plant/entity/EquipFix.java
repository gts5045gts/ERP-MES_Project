package com.erp_mes.mes.plant.entity;

import java.sql.Timestamp;

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
@Table(name = "equipment")
public class EquipFix {

	@Id
	@JoinColumn(name = "equip_id", referencedColumnName = "equip_ID")
	private String equipId;
	
	@Column(nullable = false, name = "note")
	private String note;
	
	@Column( name = "start_dt")
	private Timestamp startDt;
	
	@Column(name = "end_dt")
	private Timestamp endDt;
	
	
	
	
	
	
}
