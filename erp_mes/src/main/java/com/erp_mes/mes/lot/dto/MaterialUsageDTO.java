package com.erp_mes.mes.lot.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
public class MaterialUsageDTO {
    @NotBlank
    private String parentLotId; // 부모 LOT(원자재/상위)

    // 미지정 시 서비스에서 현재 생성 LOT(lotId)로 기본 세팅
    private String childLotId; // 자식 LOT(생산 결과)

    @Positive
    private Integer qtyUsed; // 사용 수량

    // 선택: 기록 시간
    private LocalDateTime createdAt;
}