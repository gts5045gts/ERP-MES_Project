package com.erp_mes.mes.business.dto;

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
public class OrderDTO {
	private String orderId;					// 수주 id ORD-yyyymmdd-0001
	private LocalDateTime orderDate;		// 수주 등록일(생성일)
	private String orderStatus;				// 수주 진행 상태
	private String clientId;				// 거래처 id
	private String clientName;				// 품목명
	private Integer totalOrderQty;			// 수주 총 수량
	private Integer totalOrderPrice;		// 수주 총 금액
	private Date deliveryDate;				// 납품예정일
	private String empId;					// 수주 등록자 (= 로그인한 사용자)
	private String empName;
	private LocalDateTime updateAt;			// 수정일
	private String reason;					// 취소사유
	
	private List<OrderDetailDTO> items;
}
