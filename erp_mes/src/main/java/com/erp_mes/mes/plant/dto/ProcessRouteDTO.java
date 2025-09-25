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
public class ProcessRouteDTO {
	private Long routeId; 			//라우트 아이디 기본키 
	private Long proSeq;			//공정 순서 
	private String note;			//설명  
	private String lotID;
	//조인받을 곳 타입
	private Long proId;				//공정 아이디 
	private String proNm;			//공정 이름
	private String productId; 		//제품 아이디
	private String productNm;		//제품 이름
	private String equipId;			//설비 아이디
	private String equipNm;			//설비 이름
	
	//추가된 내용 붐 아이디안에 자재정보---------------------------------------------
	
	private String bomId;			//작업지시서 아이디
	private String materialId;		//bom 안에 있는 자재 아이디
	private String materialNm;		//bom 안에 있는 자재 아이디
	
//	private String lineId;			//라인 컬럼 삭제
	
	
	
	
	
	
}
