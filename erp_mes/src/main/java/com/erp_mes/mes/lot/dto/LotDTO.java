package com.erp_mes.mes.lot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class LotDTO {
	private String lotId;
	private String targetId; // 각 테이블의 ID(PK)
	private String tableName; // 조회할 대상 테이블명
	private String type; // 각 개발자가 저장해야하는 데이터
	private String materialCode;
	private int qty;
	private String machineId;
	
	@Builder
	public LotDTO(String lotId, String targetId, String tableName, String type, String materialCode, int qty,
			String machineId) {
		this.lotId = lotId;
		this.targetId = targetId;
		this.tableName = tableName;
		this.type = type;
		this.materialCode = materialCode;
		this.qty = qty;
		this.machineId = machineId;
	}
	
	
	
}

