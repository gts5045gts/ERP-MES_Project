package com.erp_mes.mes.plant.dto;

import java.sql.Timestamp;

import com.erp_mes.erp.personnel.dto.PersonnelDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProcessDTO {
	private Long proId; 		//설비 코드 
	private String proNm; 		//설비 이름
	private String note;		//설비 설명 
	private String typeNm;		

	private Long inspecId;	//검사유형 id 
	private String inspecNm;	//검사유형 이름
	
	
	
	
	
}
