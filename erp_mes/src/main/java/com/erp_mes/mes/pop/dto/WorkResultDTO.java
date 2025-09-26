package com.erp_mes.mes.pop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.erp_mes.mes.plant.dto.ProcessDTO;
import com.erp_mes.mes.pm.dto.BomDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import groovy.transform.ToString;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class WorkResultDTO {
	
	private Long resultId;		// 실적 id
	private Long workOrderId; 		// 작업지시 id (fk)
	private Long goodQty;			// 생산수량
	private Long defectItemId;		// 항목 ID(불량) (fk)
	private Long defectQty = 0L;			// 불량수량
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt; // 등록시간
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt; // 수정시간
	
	// ========================================
	
	private String bomId;
	private Long processId;	// 공정 id
    private String equipmentId;	// 설비 id
    private String empId;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDateTime startDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDateTime endDate;
    private String workOrderStatus; // 상태
    private String empNm; // 작업자 이름
	
    //=============================================
    
    // BOM
    private String productId;	// 제품
    private String materialId;	// 자재
    private BigDecimal quantity;// 수량

    // 설비
    private String equipmentNm;	// 설비명

    // 공정
    private String processNm;	// 공정명
    
    // 자재
    private String materialNm;	// 자재명
    
    // 불량사유
    private String defectReason; // 불량사유
    
    // 제품
    private String productNm; 	// 제품명
    
    // 라우팅
    private Long routeId;		// 라우트
    private Long proSeq;		// 공정순서
    
    //생산계획
    private Long planQty;		// 목표 수량



}
