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
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LotDTO {

	@NotBlank
	private String tableName; // 대상 테이블명 (예: MATERIAL, ORDER 등)
	
	@NotBlank
	private String type; // 입고는 RM(row material), 그외 PD(product)
	
	@NotBlank
	private String materialCode; // 자재/품목 코드
	
	private String machineId; // 생산 LOT이면 설비/라인 ID, 원자재/출하는 null 가능
	
	@NotBlank
	private String targetId; // 각 테이블 PK(문자/숫자 모두 수용)
	
	@NotBlank
	private String targetIdValue; // 각 테이블 PK의 값
	
	private String lotId; // 생성된 LOT ID(Prefix+날짜+[-machine]+-SEQ)
	
	private Long workOrderId;// 작업지시 Id
	
	private LocalDateTime createdAt; // 선택: 서비스에서 세팅
	
	private String worOrderStatus;
	
	private String productName;
	
	private String productId;
	
	// 선택 연관 입력
	private List<MaterialUsageDTO> usages; // 자재 사용 내역(있을 때만 저장)
//	private List<ProcessHistoryDTO> processes; // 공정 이력(있을 때만 저장) //작업지시 참조로 변경
	
	@Builder
	public LotDTO(@NotBlank String tableName, @NotBlank String type, @NotBlank String materialCode,
			String machineId, @NotBlank String targetId, @NotBlank String targetIdValue, String lotId, Long workOrderId, LocalDateTime createdAt,
			List<MaterialUsageDTO> usages, List<ProcessHistoryDTO> processes) {
		this.tableName = tableName;
		this.type = type;
		this.materialCode = materialCode;
		this.machineId = machineId;
		this.targetId = targetId;
		this.targetIdValue = targetIdValue;
		this.lotId = lotId;
		this.workOrderId = workOrderId;
		this.createdAt = createdAt;
		this.usages = usages;
//		this.processes = processes;
	}
	
	
}