package com.erp_mes.mes.plant.dto;

import java.time.LocalDate;

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
public class EquipFixDTO {
	
	private String equipId;
	private String equipNm;
	
	private String note;
	private LocalDate startDt;
	private LocalDate endDt;
	

}
