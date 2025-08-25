package com.bootstrap.study.attendance.dto;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;

import com.bootstrap.study.attendance.entity.Annual;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AnnualDTO {

	private Long id;   // pk 용
	private String empId; // 사원Id 
	private String annYear; // 사용연도 
	private Double annUse = 0.0; // 사용연차
	private Double annTotal = 0.0; // 총연차
	private LocalDateTime createdAt; // 등록일
	private LocalDateTime updatedAt; // 수정일
	
	// 변환
	public static ModelMapper modelMapper = new ModelMapper();
	
	public Annual toEntity() {
		return modelMapper.map(this, Annual.class);
	}
	
	public static AnnualDTO fromEntity(Annual annual) {
		return modelMapper.map(annual, AnnualDTO.class);
		
	}
	
	
}
