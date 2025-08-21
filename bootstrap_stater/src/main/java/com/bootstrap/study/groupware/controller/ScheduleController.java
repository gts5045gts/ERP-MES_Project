package com.bootstrap.study.groupware.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootstrap.study.groupware.dto.ScheduleDTO;
import com.bootstrap.study.groupware.entity.Schedule;
import com.bootstrap.study.groupware.service.ScheduleService;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/schedule")
@Log4j2
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    // 일정 목록 페이지를 보여주는 메서드
    @GetMapping("")
    public String scheduleList(Model model) {
    	log.info("ScheduleController scheduleList()");
        List<Schedule> schedules = scheduleService.findAllSchedules();
//        List<ScheduleDTO> scheduleDTOs = schedules.stream()
//                .map(ScheduleDTO::new)
//                .collect(Collectors.toList());
//                
//            model.addAttribute("schedules", scheduleDTOs);
        return "gw/schedule";
    }

    // 일정 등록 페이지를 보여주는 메서드 (schWrite.html)
    @GetMapping("/write")
    public String writeForm() {
        return "gw/schWrite";
    }

    // ⭐ 일정 등록 (모달 & 페이지 폼 제출)을 처리하는 메서드
    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> saveSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        log.info("ScheduleController saveSchedule()", scheduleDTO);
        
        Schedule schedule = new Schedule();
        // 이 과정에서 임시 ID와 타입을 할당합니다.
        schedule.setEmpId(scheduleDTO.getEmpId());
        schedule.setSchTitle(scheduleDTO.getSchTitle());
        schedule.setSchContent(scheduleDTO.getSchContent());
        schedule.setStarttimeAt(scheduleDTO.getStarttimeAt());
        schedule.setEndtimeAt(scheduleDTO.getEndtimeAt());
        schedule.setSchType("전체"); 
        
        scheduleService.saveSchedule(schedule);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "일정이 성공적으로 등록되었습니다.");
        return response;
        
    }
    
 // 전체 일정 데이터
    @GetMapping("/events/all")
    @ResponseBody
    public List<Map<String, Object>> getAllSchedules() {
    	log.info("ScheduleController getAllschedules()");
    	
        List<Schedule> schedules = scheduleService.findAllSchedules();
        List<Map<String, Object>> events = schedules.stream()
            .map(schedule -> {
                Map<String, Object> event = new HashMap<>();
                event.put("id", schedule.getSchId());
                event.put("title", schedule.getSchTitle());
                event.put("start", schedule.getStarttimeAt());
                event.put("end", schedule.getEndtimeAt());
                event.put("schType", schedule.getSchType());
                event.put("empId", schedule.getEmpId());
                // 추가 필드가 있다면 여기에 포함
                return event;
            })
            .collect(Collectors.toList());
        return events;
    }

    // 부서별 일정 데이터
    @GetMapping("/events/dept")
    @ResponseBody
    public List<Map<String, Object>> getDeptSchedules() {
        // 부서별 일정을 가져오는 서비스 로직 구현 필요
        // 예시: List<Schedule> deptSchedules = scheduleService.findDeptSchedules();
        // 현재는 모든 일정을 반환하는 로직과 동일하게 작성
        List<Schedule> schedules = scheduleService.findAllSchedules();
        List<Map<String, Object>> events = schedules.stream()
            .map(schedule -> {
                Map<String, Object> event = new HashMap<>();
                event.put("id", schedule.getSchId());
                event.put("title", schedule.getSchTitle());
                event.put("start", schedule.getStarttimeAt());
                event.put("end", schedule.getEndtimeAt());
                return event;
            })
            .collect(Collectors.toList());
        return events;
    }
    
    @GetMapping("/{schId}")
    @ResponseBody
    public Schedule getScheduleDetail(@PathVariable("schId") Long schId) {
        log.info("Requesting schedule detail for ID: {}", schId);
        return scheduleService.findById(schId);
    }
}