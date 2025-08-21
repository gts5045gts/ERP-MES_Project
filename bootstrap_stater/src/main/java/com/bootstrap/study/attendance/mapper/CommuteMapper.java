package com.bootstrap.study.attendance.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bootstrap.study.attendance.dto.CommuteDTO;
import com.bootstrap.study.attendance.dto.CommuteScheduleDTO;

@Mapper
public interface CommuteMapper {

	// 출퇴근리스트
	List<CommuteDTO> getCommuteList(String empId);
	
	// 출근했을때 데이터 commute_record 테이블에 저장
	void insertCommute(CommuteDTO commute);

}
