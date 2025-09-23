package com.erp_mes.mes.plant.entity;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.mes.plant.dto.ProcessDTO;
import com.erp_mes.mes.quality.entity.InspectionFM;
import com.erp_mes.mes.quality.repository.InspectionFMRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
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
	@Column( name = "PRO_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "process_seq")
	@SequenceGenerator(
		    name = "process_seq",
		    sequenceName = "process_seq",
		    allocationSize = 1 // DB 시퀀스랑 동일하게!
		)
	private Long proId;
	
	@Column(nullable = false, name = "PRO_NM")
	private String proNm;
	
	
	@Column(nullable = false, name = "NOTE")
	private String note;
	
	@Column(nullable = false, name = "TYPE_NM")
	private String typeNm;
	
	//추가된 참조 컬럼
	@ManyToOne	
	@JoinColumn(name = "inspection_fm_id", referencedColumnName = "inspection_fm_id")
	private InspectionFM inspectionFm;
	
	static public Process fromDTO(ProcessDTO proDTO, InspectionFMRepository repo) {
		Process pro = new Process();
		pro.setProId(proDTO.getProId());
		pro.setProNm(proDTO.getProNm());
		pro.setNote(proDTO.getNote());
		pro.setTypeNm(proDTO.getTypeNm());
		
		//검사유형 추가
		if(proDTO.getInspecId() != null) {
			InspectionFM inpec = repo.findById(proDTO.getInspecId())
					 .orElseThrow(() -> new IllegalArgumentException("없는 공정검사 코드!! -------------------"));
			pro.setInspectionFm(inpec);
		}
		
		
		return pro;
	}
	
	

}