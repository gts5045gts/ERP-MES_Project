package com.bootstrap.study.commonCode.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.bootstrap.study.commonCode.entity.CommonDetailCode;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Service
@ToString
public class CommonDetailCodeDTO {

	private String comId; // 코드(fk)
	private String comDtId; // 상세코드(pk)
	private String comDtNm; // 코드명
	private String useYn; // 사용여부
	private Integer comDtOrder; // 상세코드정렬순서
	private String createdAt; // 등록일
	private LocalDateTime updatedAt; // 수정일 
	
	
	
	public static ModelMapper modelMapper = new ModelMapper();
	
	public CommonDetailCode toEntity() {
		return modelMapper.map(this, CommonDetailCode.class);
	}

	public static CommonDetailCodeDTO fromEntity(CommonDetailCode commonDetail) {
		CommonDetailCodeDTO dto = modelMapper.map(commonDetail, CommonDetailCodeDTO.class);
		if (commonDetail.getCreatedAt() != null) {
		    dto.setCreatedAt(commonDetail.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		}
		return dto;
	}
	
}
