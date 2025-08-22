package com.bootstrap.study.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bootstrap.study.attendance.dto.CommuteDTO;
import com.bootstrap.study.attendance.dto.CommuteScheduleDTO;
import com.bootstrap.study.attendance.mapper.CommuteMapper;
import com.bootstrap.study.attendance.mapper.CommuteScheduleMapper;

@Service
public class CommuteService {

	private final CommuteMapper commuteMapper;
	private final CommuteScheduleMapper commuteScheduleMapper;

	public CommuteService(CommuteMapper commuteMapper, CommuteScheduleMapper commuteScheduleMapper) {
		this.commuteMapper = commuteMapper;
		this.commuteScheduleMapper = commuteScheduleMapper;
	}

	// 출근현황 리스트
//	public List<CommuteDTO> getDeptCommuteList(String empId, LocalDate queryDate) {
	public List<CommuteDTO> getDeptCommuteList(Map<String, Object> paramMap) {
		
		return commuteMapper.getDeptCommuteList(paramMap);
	}

	// 출근버튼
	public CommuteDTO checkIn(String empId) {
		
		LocalDateTime now = LocalDateTime.now();
		
		// 근무 기준
		CommuteScheduleDTO schedule = commuteScheduleMapper.getCurrentSchedule();
		LocalTime startTime = schedule.getWorkStartTime().toLocalTime(); // db에서 가져온 출근시작시간
		LocalTime nowTime = LocalDateTime.now().toLocalTime(); // 현재시간
		
		// 지각 여부 판별
		String workStatus = nowTime.isAfter(startTime) ? "지각" : "출근";
		
		CommuteDTO commute = new CommuteDTO();
		commute.setEmpId(empId);
		commute.setCheckInTime(now);
		commute.setWorkStatus(workStatus);

		commuteMapper.insertCommute(commute);
		System.out.println("commute : " + commute);
		
		return commute;

	}

}
