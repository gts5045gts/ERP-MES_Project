package com.erp_mes.mes.pop.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.erp_mes.mes.pop.dto.DefectDTO;
import com.erp_mes.mes.pop.dto.WorkResultDTO;

@Mapper
public interface DefectMapper {

	// 불량 테이블에 저장
	void saveDefect(DefectDTO defectDTO);
	


	// 불량 업데이트
	int updateDefect(DefectDTO defectDTO);

	
	
	




}
