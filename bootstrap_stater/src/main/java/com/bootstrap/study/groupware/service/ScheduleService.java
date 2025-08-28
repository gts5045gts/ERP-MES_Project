package com.bootstrap.study.groupware.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.bootstrap.study.groupware.entity.Schedule;
import com.bootstrap.study.groupware.repository.ScheduleRepository;

@Service
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;

	public ScheduleService(ScheduleRepository scheduleRepository) {
		this.scheduleRepository = scheduleRepository;
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

	public void updateSchedule(Schedule schedule) {
		// 기존 엔티티를 찾아 업데이트 (일부 필드만 업데이트 시 필요)
		Optional<Schedule> existingSchedule = scheduleRepository.findById(schedule.getSchId());
		if (existingSchedule.isPresent()) {
			Schedule updatedSchedule = existingSchedule.get();
			updatedSchedule.setSchTitle(schedule.getSchTitle());
			updatedSchedule.setSchContent(schedule.getSchContent());
			updatedSchedule.setStarttimeAt(schedule.getStarttimeAt());
			updatedSchedule.setEndtimeAt(schedule.getEndtimeAt());
			scheduleRepository.save(updatedSchedule);
		}
	}

	// 일정 작성자 권한 확인
	public boolean isScheduleOwner(Long schId, Long empId) {
	    	Optional<Schedule> schedule = scheduleRepository.findById(schId);
	    	return schedule.isPresent() && schedule.get().getEmpId().equals(empId);
	    }

	public List<Schedule> findByEmpDeptName(String empDeptName) {
		
		return scheduleRepository.findByschType(empDeptName);
	}
}