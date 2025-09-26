package com.erp_mes.mes.purchase.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PurchaseDetailDTO {
	private Integer id;
	private String purchaseId;		// 발주 참조
	private String unit;			// 공통코드 참조
	private String materialId;		
	private String materialName;
	private Integer purchaseQty;
	private Integer purchasePrice;
	private String purchaseDetailStatus;
	private LocalDateTime createAt;
	private LocalDateTime updateAt;
	
	private Integer totalPrice;			// 발주 상세 목록에서 총금액(= 수량*단가) 조회용
}
