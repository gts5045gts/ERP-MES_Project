package com.bootstrap.study.commonCode.dto;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.bootstrap.study.commonCode.entity.CommonCode;
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
	private LocalDateTime createdAt; // 등록일
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt; // 수정일 
	
	
	// 변환
	public static ModelMapper modelMapper = new ModelMapper();
	
	public CommonCode toEntity() {
		return modelMapper.map(this, CommonCode.class);
	}
	
	public static CommonCodeDTO fromEntity(CommonCode commonCode) {
		return modelMapper.map(commonCode, CommonCodeDTO.class);
	}
	
	
}
