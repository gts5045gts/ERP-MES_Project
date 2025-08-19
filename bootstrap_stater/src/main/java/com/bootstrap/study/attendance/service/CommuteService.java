package com.bootstrap.study.attendance.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bootstrap.study.attendance.dto.CommuteDTO;
import com.bootstrap.study.attendance.dto.CommuteScheduleDTO;
import com.bootstrap.study.attendance.mapper.CommuteMapper;

@Service
public class CommuteService {

	private final CommuteMapper commuteMapper;
	
	public CommuteService(CommuteMapper commuteMapper) {
		this.commuteMapper = commuteMapper;
	}


	// 출근현황 리스트
	public List<CommuteDTO> getCommuteList() {
		
		List<CommuteDTO> commuteDTOList = commuteMapper.selectAllCommute();
		System.out.println("commuteDTOList : " + commuteDTOList);
		
		if(!commuteDTOList.isEmpty()) {
			System.out.println("첫번째 항목 : " + commuteDTOList.get(0));
		}
		
		return null;
	}


	public void checkIn(String empId) {
		CommuteScheduleDTO schedule = commuteMapper.getSchedule();
		
		// 근무 기준
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime workStart = schedule.getWorkStartTime();
		
		// 출근 상태 판별
		String workStatus;
		if(now.isAfter(workStart)) {
			workStatus = "지각";
		} else {
			workStatus = "출근";
		}
		
		CommuteDTO commute = new CommuteDTO();
		commute.setEmpId(empId);
		commute.setCheckInTime(now);
		commute.setWorkStatus(workStatus);
		
		commuteMapper.insertCommute(commute);
		
	}

}
