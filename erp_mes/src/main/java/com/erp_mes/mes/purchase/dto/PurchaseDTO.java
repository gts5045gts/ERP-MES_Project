package com.erp_mes.mes.purchase.dto;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PurchaseDTO {
	private String purchaseId;				// 발주 id PUR-yyyymmdd-0001
	private Date purchaseDate;				// 발주 등록일(생성일)
	private String purchaseStatus;			// 발주 진행 상태
	private String clientId;				// 거래처 id
	private Integer totalPurchaseQty;		// 발주 총 수량
	private Integer totalPurchasePrice;		// 발주 총 금액
	private Date inputDate;					// 입고예정일
	private String empId;					// 발주 등록자 (= 로그인한 사용자)
	private LocalDateTime updateAt;			// 수정일
	private String purchaseType;
	private String reason; 					// 취소사유
	
	private String clientName; // 조회용
	private String empName; // 조회용
	
	private List<PurchaseDetailDTO> materials;
	
	private String workOrderId;
}
