package com.erp_mes.mes.lot.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class LotDTO {

	@NotBlank
	private String tableName; // 대상 테이블명 (예: MATERIAL, ORDER 등)
	
	@NotBlank
	private String type; // 입고는 RM(row material), 그외 PD(product)
	
	@NotBlank
	private String materialCode; // 자재/품목 코드
	
	@Min(0)
	private int qty; // LOT 수량(기본 0 가능)
	
	private String machineId; // 생산 LOT이면 설비/라인 ID, 원자재/출하는 null 가능
	
	@NotBlank
	private String targetId; // 각 테이블 PK(문자/숫자 모두 수용)
	
	private String lotId; // 생성된 LOT ID(Prefix+날짜+[-machine]+-SEQ)
	
	private Long workOrderId;// 작업지시 Id
	
	private LocalDateTime createdAt; // 선택: 서비스에서 세팅
	
	// 선택 연관 입력
	private List<MaterialUsageDTO> usages; // 자재 사용 내역(있을 때만 저장)
//	private List<ProcessHistoryDTO> processes; // 공정 이력(있을 때만 저장) //작업지시 참조로 변경
	
	@Builder
	public LotDTO(@NotBlank String tableName, @NotBlank String type, @NotBlank String materialCode, @Min(0) int qty,
			String machineId, @NotBlank String targetId, String lotId, Long workOrderId, LocalDateTime createdAt,
			List<MaterialUsageDTO> usages, List<ProcessHistoryDTO> processes) {
		this.tableName = tableName;
		this.type = type;
		this.materialCode = materialCode;
		this.qty = qty;
		this.machineId = machineId;
		this.targetId = targetId;
		this.lotId = lotId;
		this.workOrderId = workOrderId;
		this.createdAt = createdAt;
		this.usages = usages;
//		this.processes = processes;
	}
	
	
}
