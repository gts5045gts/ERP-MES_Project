package com.bootstrap.study.groupware.service;

import java.util.List;
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
		// TODO Auto-generated method stub
		
	}

	public void updateSchedule(Schedule schedule) {
		// TODO Auto-generated method stub
		
	}
}