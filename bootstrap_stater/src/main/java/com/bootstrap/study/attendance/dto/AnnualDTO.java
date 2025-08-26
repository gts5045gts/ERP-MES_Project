package com.bootstrap.study.attendance.dto;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;

import com.bootstrap.study.attendance.entity.Annual;
import com.bootstrap.study.personnel.entity.Personnel;

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
	private Double annRemain = 0.0; // 잔여연차
	private Double annTotal = 0.0; // 총연차
	private LocalDateTime createdAt; // 등록일
	private LocalDateTime updatedAt; // 수정일
	
	private String empName; // 사원이름
	private String depName; // 부서(공통코드)
	private String empPos; // 직책 (공통코드)
	private String joinDate; // 입사일

	
	
	
	// 변환
	public static ModelMapper modelMapper = new ModelMapper();
	
	public Annual toEntity() {
		return modelMapper.map(this, Annual.class);
	}
	
	public static AnnualDTO fromEntity(Annual annual) {
		return modelMapper.map(annual, AnnualDTO.class);
		
	}

	public AnnualDTO(Annual annual, Personnel personnel) {
        this.empId = annual.getEmpId();
        this.annYear = annual.getAnnYear();
        this.annUse = annual.getAnnUse();
        this.annRemain = annual.getAnnTotal() - annual.getAnnUse();
        this.annTotal = annual.getAnnTotal();
        this.createdAt = annual.getCreatedAt();
        this.updatedAt = annual.getUpdatedAt();
        this.empName = personnel.getName();
        this.depName = personnel.getDepartment().getComDtNm();
        this.empPos = personnel.getPosition().getComDtNm();
        this.joinDate = personnel.getJoinDate();
    }
	
	
}
