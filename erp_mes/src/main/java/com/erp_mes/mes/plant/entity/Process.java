package com.erp_mes.mes.plant.entity;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.mes.plant.dto.ProcessDTO;

import jakarta.persistence.Column;
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
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "PRO_TYPE", referencedColumnName = "COM_DT_ID")
	private CommonDetailCode common;
	
	static public Process fromDTO(ProcessDTO proDTO, CommonDetailCodeRepository repo) {
		Process pro = new Process();
		pro.setProId(proDTO.getProId());
		pro.setProNm(proDTO.getProNm());
		pro.setNote(proDTO.getNote());
		
		if(proDTO.getTypeId() != null) {
			CommonDetailCode compro = repo.findById(proDTO.getTypeId())
					 .orElseThrow(() -> new IllegalArgumentException("없는 부서 코드"));
			pro.setCommon(compro);
		}
		return pro;
	}
	
	

}
