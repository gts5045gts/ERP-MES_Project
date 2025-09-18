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
	
	//조인받을 곳 타입
	private String typeId;
	private String typeNm;
	
	
	
	public static ProcessDTO formEntity(Process proc) {
		
		return null;
	}
	
}
