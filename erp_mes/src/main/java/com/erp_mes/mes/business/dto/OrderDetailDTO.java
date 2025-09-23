package com.erp_mes.mes.business.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderDetailDTO {
	private Integer id;
	private String orderId;
	private String unit;
	private String productId;
	private String productName;
	private Integer orderQty;
	private Integer orderPrice;
	private String orderDetailStatus;
	private Integer totalPrice;			// 수주 상세 목록에서 총금액(= 수량*단가)
	private LocalDateTime createAt;
	private LocalDateTime updateAt;
	
	private Integer stockQty; // 출하에서 필요
	private int remainingQty;
	private boolean isDelayed; // 날짜 지연 여부(출하 제한을 위해 추가)
}
