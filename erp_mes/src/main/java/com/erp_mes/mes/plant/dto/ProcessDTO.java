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
	private String proId; 
	private String proNm; 
	private String typeId;
	private String typeNm;
	private String note;
	
	
	
	public static ProcessDTO formEntity(Process proc) {
		
		return null;
	}
	
}
