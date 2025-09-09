package com.erp_mes.erp.attendance.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;

import com.erp_mes.erp.attendance.dto.CommuteScheduleDTO;

@Mapper
public interface CommuteScheduleMapper {

	// 현재 근무스케쥴(지각유무 판단할 수 있는 값)
	CommuteScheduleDTO getCurrentSchedule();

}
