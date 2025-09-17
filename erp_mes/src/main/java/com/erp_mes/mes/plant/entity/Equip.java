package com.erp_mes.mes.plant.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity
@Table(name = "equipment")
public class Equip {

	@Id
	@Column(name = "equip_id")
	private String equipId;
	
	@Column(nullable = false, name = "equip_nm")
	private String equipNm;
	
	@Column(nullable = false, name = "use_yn")
	private String useYn;
	
	@Column(nullable = false, name = "note")
	private String note;
	
	@Column( name = "purchase_dt")
	private Timestamp purchaseDt;
	
	@Column(nullable = false, name = "install_dt")
	private Timestamp installDt;
	
	
	
	
	
	
}
