package com.erp_mes.erp.approval.dto;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;

import com.erp_mes.erp.approval.constant.ApprDecision;
import com.erp_mes.erp.approval.entity.ApprLine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ApprLineDTO {
	
	private	Long lineId;
	private	Integer	reqId; //결제문서 id
	private	Integer	stepNo; // 결제 순번 
	private	String	apprId; //결제자 id
	private ApprDecision decision;//승인, 반려
	private	LocalDateTime decDate;
	private	String	comments;
	
//	vac_type 타입 이 전일일때
//	if (ApprDTO.get() == null) {
//	    // 전일 휴가 처리
//	}
	
	private static ModelMapper modelMapper = new ModelMapper();

	public ApprLine toEnity() { return modelMapper.map(this, ApprLine.class); }
	
	public static ApprLineDTO fromEntity(ApprLine appLine) { return modelMapper.map(appLine, ApprLineDTO.class); }
}
