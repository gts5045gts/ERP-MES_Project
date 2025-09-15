package com.erp_mes.erp.commonCode.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.erp_mes.erp.commonCode.entity.CommonCode;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CommonCodeDTO {

	private String comId; // 코드
	private String comNm; // 코드명
	private String useYn; // 사용여부
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private String createdAt; // 등록일
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt; // 수정일 
	
	private String systemType; // ERP인지 MES인지
	
	
	// 변환
	public static ModelMapper modelMapper = new ModelMapper();
	
	public CommonCode toEntity() {
		return modelMapper.map(this, CommonCode.class);
	}
	
	public static CommonCodeDTO fromEntity(CommonCode commonCode) {
		CommonCodeDTO dto = modelMapper.map(commonCode, CommonCodeDTO.class);
		if (commonCode.getCreatedAt() != null) {
		    dto.setCreatedAt(commonCode.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		}
		return dto;
	}
	
	
}
