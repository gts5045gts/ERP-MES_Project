package com.erp_mes.mes.plant.entity;

import java.time.LocalDate;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.mes.plant.dto.EquipDTO;

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
	private LocalDate purchaseDt;
	
	@Column(nullable = false, name = "install_dt")
	private LocalDate installDt;
	
	
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "TYPE_ID", referencedColumnName = "COM_DT_ID")
	private CommonDetailCode common;

	static public Equip fromDTO(EquipDTO equipDTO, CommonDetailCodeRepository repo) {
		Equip equip = new Equip();
		equip.setEquipId(equipDTO.getEquipId());
		equip.setEquipNm(equipDTO.getEquipNm());
		equip.setUseYn(equipDTO.getUseYn());
		equip.setNote(equipDTO.getNote());
		equip.setInstallDt(equipDTO.getInstallDt());
		equip.setPurchaseDt(equipDTO.getPurchaseDt());
		
		if(equipDTO.getTypeId() != null) {
			CommonDetailCode comEquip = repo.findById(equipDTO.getTypeId())
					 .orElseThrow(() -> new IllegalArgumentException("없는 공정 코드"));
			equip.setCommon(comEquip);
		}
		return equip;
	}
	
	
	
	
	
	
}
