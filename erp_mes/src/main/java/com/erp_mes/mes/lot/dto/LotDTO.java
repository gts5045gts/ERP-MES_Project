package com.erp_mes.mes.lot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LotDTO {
	private String lotId;
	private String targetId; // 각 테이블의 ID(PK)
	private String tableName; // 조회할 대상 테이블명
	private String type; // 각 개발자가 저장해야하는 데이터
	private String materialCode;
	private int qty;
	private String machineId;
	
}

