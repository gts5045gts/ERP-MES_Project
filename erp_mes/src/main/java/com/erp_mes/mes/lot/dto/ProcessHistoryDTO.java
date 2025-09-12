package com.erp_mes.mes.lot.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessHistoryDTO {

	@NotBlank
	private String processCode; // 예: Cutting, Assembly 등

	private String machineId; // 선택 입력(설비 단위 추적)

	private String operator; // 작업자 ID 또는 이름

	private LocalDateTime processStart; // 공정 시작 시간
	private LocalDateTime processEnd; // 공정 종료 시간

	@PositiveOrZero
	private Integer inputQty; // 투입 수량

	@PositiveOrZero
	private Integer resultQty; // 산출 수량(불량 제외)

	@PositiveOrZero
	private Integer scrapQty; // 불량 수량(옵션)
}
