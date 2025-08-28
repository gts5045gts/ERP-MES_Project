package com.bootstrap.study.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bootstrap.study.attendance.dto.AdminCommuteDTO;
import com.bootstrap.study.attendance.dto.CommuteDTO;
import com.bootstrap.study.attendance.dto.CommuteScheduleDTO;
import com.bootstrap.study.attendance.mapper.CommuteMapper;
import com.bootstrap.study.attendance.mapper.CommuteScheduleMapper;
import com.bootstrap.study.commonCode.dto.CommonDetailCodeDTO;
import com.bootstrap.study.personnel.dto.PersonnelDTO;

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
//	    System.out.println("count : " + count);
	    if (count > 0) {
	        throw new IllegalStateException("이미 오늘 출근 기록이 존재합니다.");
	    }
		
		// 근무 기준시간 조회
		CommuteScheduleDTO schedule = commuteScheduleMapper.getCurrentSchedule();
//		System.out.println("schedule : " + schedule);
		
		// 지각 여부 판별
		LocalTime startTime = schedule.getWorkStartTime().toLocalTime(); // db에서 가져온 출근시작시간
		// db에는 날짜로만 insert 해도 결국 년/월/일이 다 입력 되기에 toLocalTime()을 써서 시간끼리만 비교해야함
		LocalTime nowTime = LocalDateTime.now().toLocalTime(); // 현재시간
		LocalDateTime now = LocalDateTime.now();
		
	    String workStatus;
	    if (nowTime.isAfter(startTime)) {
	        workStatus = "WSTA003"; // 지각
	    } else {
	        workStatus = "WSTA001"; // 출근
	    }
		
		CommuteDTO commute = new CommuteDTO();
		commute.setEmpId(empId);
		commute.setCheckInTime(now);
		commute.setWorkStatus(workStatus);
//		System.out.println("commute : " + commute);

		commuteMapper.insertCommuteCheckIn(commute);
		
		return commute;
	}

	// 퇴근버튼
	public CommuteDTO checkOut(String empId) {
		
		// 오늘 퇴근 기록이 있는지 확인
		int count = commuteMapper.getTodayCheckOutCount(empId);
//		System.out.println("count : " + count);
		if (count > 0) {
			throw new IllegalStateException("이미 오늘 퇴근 기록이 존재합니다.");
		}
		
		
		
	    LocalDateTime now = LocalDateTime.now();
		
		CommuteDTO commute = new CommuteDTO();
		commute.setEmpId(empId);
		commute.setCheckOutTime(now);
		commute.setWorkStatus("WSTA002");
		
		commuteMapper.updateCommuteCheckOut(commute);
		
		return commute;
	}
	

	// 부서 공통코드
	public List<CommonDetailCodeDTO> getCommonDept() {
		List<CommonDetailCodeDTO> commonDept = commuteMapper.getCommonDept("DEP");
//		System.out.println("commonDept : " + commonDept);
		return commonDept;
	}

	// 전체 부서 조회
	public List<AdminCommuteDTO> getAllDeptCommuteList(Map<String, Object> paramMap) {
		return commuteMapper.getAllDeptCommuteList(paramMap);
	}
	// 특정 부서 조회
	public List<AdminCommuteDTO> getSpecificDeptCommuteList(Map<String, Object> paramMap) {
		return commuteMapper.getSpecificDeptCommuteList(paramMap);
	}

	// 근무상태 공통코드
	public List<CommonDetailCodeDTO> getCommonStatus() {
		List<CommonDetailCodeDTO> commonStatus = commuteMapper.getCommonStatus("WSTA");
//		System.out.println("commonStatus : " + commonStatus);
		return commonStatus;
	}

	// 관리자 수정버튼
	public int updateWorkStatus(List<AdminCommuteDTO> updateList) {
		int updatedCount = 0;
		
        for (AdminCommuteDTO dto : updateList) {
            updatedCount += commuteMapper.updateWorkStatus(dto); // 개별 UPDATE 호출
        }
		
	    return updatedCount;
	}
	

}
