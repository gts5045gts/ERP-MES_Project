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
@AllArgsConstructor
@Builder
public class LotDTO {

	  // lot_master 기반 입력 필드
	  @NotBlank
	  private String tableName;          // 대상 테이블명 (예: MATERIAL, ORDER 등)

	  @NotBlank
	  private String type;               // RM, PR, FG, QA 등

	  @NotBlank
	  private String materialCode;       // 자재/품목 코드

	  @Min(0)
	  private Integer qty;               // LOT 수량(기본 0 가능)

	  private String machineId;          // 생산 LOT이면 설비/라인 ID, 원자재/출하는 null 가능

	  // 다형 참조용: 운반은 String, 저장 시 DB 타입에 맞춰 변환
	  @NotBlank
	  private String targetId;           // 각 테이블 PK(문자/숫자 모두 수용)

	  // 시스템 채움 필드
	  private String lotId;              // 생성된 LOT ID(Prefix+날짜+[-machine]+-SEQ)
	  private LocalDateTime createdAt;   // 선택: 서비스에서 세팅

	  // 선택 연관 입력
	  private List<MaterialUsageDTO> usages;     // 자재 사용 내역(있을 때만 저장)
	  private List<ProcessHistoryDTO> processes; // 공정 이력(있을 때만 저장)
	}
