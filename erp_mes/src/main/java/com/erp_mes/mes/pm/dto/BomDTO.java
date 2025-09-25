package com.erp_mes.mes.pm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BomDTO {
	private String bomId;
	private String productId;
	private Integer revisionNo;
	private String materialId;
	private String materialName;
	private BigDecimal quantity; // 제품 1개당 필요한 수량
	private String unit;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDateTime createdAt;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDateTime updatedAt;
	
	// 여러 자재 추가 할때 필요
	private List<MaterialDTO> materials;
	
	// 필요한 bom 자재를 자재 수량과 비교할때 필요
    private BigDecimal totalNeededQuantity; // 제품 n개 자재 총 필요 수량
    private BigDecimal stockQuantity;     // 창고 자재 재고 수량
    private BigDecimal requiredQty ;     // 필요수량 - 재고수량
    private String workPossible;          // 작업 가능 / 작업 불가능
}
