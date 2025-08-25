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
		System.out.println("paramMap : " + paramMap);
		return commuteMapper.getDeptCommuteList(paramMap);
	}

	// 출근버튼
	public CommuteDTO checkIn(String empId) {
		
		// 오늘 출근 기록이 있는지 확인
	    int count = commuteMapper.getTodayCheckInCount(empId);
	    System.out.println("count : " + count);
	    if (count > 0) {
	        throw new IllegalStateException("이미 오늘 출근 기록이 존재합니다.");
	    }
		
		// 근무 기준시간 조회
		CommuteScheduleDTO schedule = commuteScheduleMapper.getCurrentSchedule();
		System.out.println("schedule : " + schedule);
		
		// 지각 여부 판별
		LocalTime startTime = schedule.getWorkStartTime().toLocalTime(); // db에서 가져온 출근시작시간
		LocalDateTime now = LocalDateTime.now();
		LocalTime nowTime = LocalDateTime.now().toLocalTime(); // 현재시간
		
		String workStatus = nowTime.isAfter(startTime) ? "지각" : "출근";
		
		CommuteDTO commute = new CommuteDTO();
		commute.setEmpId(empId);
		commute.setCheckInTime(now);
		commute.setWorkStatus(workStatus);
		System.out.println("commute : " + commute);

		commuteMapper.insertCommute(commute);
		
		return commute;

	}

}
