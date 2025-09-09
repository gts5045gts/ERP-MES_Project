package com.erp_mes.erp.groupware.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.erp_mes.erp.groupware.dto.ScheduleDTO;
import com.erp_mes.erp.groupware.entity.Schedule;
import com.erp_mes.erp.groupware.repository.ScheduleRepository;
import com.erp_mes.erp.personnel.entity.Personnel;
import com.erp_mes.erp.personnel.repository.PersonnelRepository;

@Service
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final PersonnelRepository personnelRepository;

	public ScheduleService(ScheduleRepository scheduleRepository, PersonnelRepository personnelRepository) {
		this.scheduleRepository = scheduleRepository;
		this.personnelRepository = personnelRepository;
	}

	public List<Schedule> findAllSchedules() {
		return scheduleRepository.findAll();
	}

	public void saveSchedule(Schedule schedule) {
		scheduleRepository.save(schedule);
	}

	public Schedule findById(Long schId) {
		return scheduleRepository.findById(schId).orElse(null);
	}

	public void deleteSchedule(Long schId) {
		scheduleRepository.deleteById(schId);
	}

	public void updateSchedule(ScheduleDTO scheduleDTO) {
		// 기존 엔티티를 찾아 업데이트 (일부 필드만 업데이트 시 필요)
		Optional<Schedule> existingSchedule = scheduleRepository.findById(scheduleDTO.getSchId());
		if (existingSchedule.isPresent()) {
			Schedule updatedSchedule = existingSchedule.get();
			updatedSchedule.setSchTitle(scheduleDTO.getSchTitle());
			updatedSchedule.setSchContent(scheduleDTO.getSchContent());
			updatedSchedule.setStarttimeAt(scheduleDTO.getStarttimeAt());
			updatedSchedule.setEndtimeAt(scheduleDTO.getEndtimeAt());
			scheduleRepository.save(updatedSchedule);
		}
	}

	// 일정 작성자 권한 확인
	public boolean isScheduleOwner(Long schId, String empId) {
	    Optional<Schedule> schedule = scheduleRepository.findById(schId);
	    // equals() 대신 Objects.equals()를 사용하여 NPE 방지
	    return schedule.isPresent() && Objects.equals(schedule.get().getEmployee().getEmpId(), empId);
	}

	public List<Schedule> findByEmpDeptName(String empDeptName) {
		
		return scheduleRepository.findByschType(empDeptName);
	}

	public String getEmpNameById(String empId) {
		// Personnel 엔티티를 조회하여 이름(name)을 반환
		return personnelRepository.findById(empId)
			.map(Personnel::getName)
			.orElse(null); // 사원을 찾지 못하면 null 반환
	}
}