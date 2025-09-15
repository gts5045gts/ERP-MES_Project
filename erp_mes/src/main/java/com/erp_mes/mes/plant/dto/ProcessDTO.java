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
	private String proId; 		//공정 코드 
	private String proNm; 		//공정 이름
	private String note;		//공정 설명 
	
	//조인받을 곳 타입
	private String typeId;
	private String typeNm;
	
	
	
	public static ProcessDTO formEntity(Process proc) {
		
		return null;
	}
	
}
