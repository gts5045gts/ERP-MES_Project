package com.erp_mes.erp.commonCode.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CommonDetailCodeDTO {

	private String comId; // 코드(fk)
	private String comDtId; // 상세코드(pk)
	private String comDtNm; // 코드명
	private String useYn; // 사용여부
	private Integer comDtOrder; // 상세코드정렬순서
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt; // 등록일
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt; // 수정일 
	
	
	
	public static ModelMapper modelMapper = new ModelMapper();
	
	public CommonDetailCode toEntity() {
		return modelMapper.map(this, CommonDetailCode.class);
	}

	public static CommonDetailCodeDTO fromEntity(CommonDetailCode commonDetail) {
		return modelMapper.map(commonDetail, CommonDetailCodeDTO.class);
	}
	
	 public CommonDetailCodeDTO(String comId, String comDtNm) {
		 this.comId = comId;
		 this.comDtNm = comDtNm;
	 }

	
}
