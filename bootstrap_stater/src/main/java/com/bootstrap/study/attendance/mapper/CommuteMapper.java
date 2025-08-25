package com.bootstrap.study.attendance.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.bootstrap.study.attendance.dto.CommuteDTO;
import com.bootstrap.study.attendance.dto.CommuteScheduleDTO;

@Mapper
public interface CommuteMapper {

	// 출퇴근리스트
	List<CommuteDTO> getDeptCommuteList(Map<String, Object> paramMap);
	
	// 오늘 출근 기록이 있는지 확인
	int getTodayCheckInCount(String empId);
	
	// 출근버튼 눌렀을때 데이터 commute_record 테이블에 저장
	int insertCommute(CommuteDTO commute);


}
