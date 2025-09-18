package com.erp_mes.mes.plant.entity;

import java.time.LocalDate;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.mes.plant.dto.EquipFixDTO;
import com.erp_mes.mes.plant.dto.ProcessDTO;

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
@Entity
@Table(name = "equipment_fix")
public class EquipFix {

	@Id
	@Column(name = "equip_id")
	private String equipId;
	
	@ManyToOne
	@JoinColumn(name = "equip_id", referencedColumnName = "equip_id", insertable = false, updatable = false)
	private Equip equip;
	
	@Column(nullable = false, name = "note")
	private String note;
	
	@Column( name = "start_dt")
	private LocalDate startDt;
	
	@Column(name = "end_dt")
	private LocalDate endDt;
	
	
	
	static public EquipFix fromDTO(EquipFixDTO eDTO) {
		EquipFix equip = new EquipFix();
		equip.setEquipId(eDTO.getEquipId());
		equip.setNote(eDTO.getNote());
		equip.setStartDt(eDTO.getStartDt());
		equip.setEndDt(eDTO.getEndDt());
		
		
		return equip;
	}
	
	
	
}
