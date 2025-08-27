package com.bootstrap.study.groupware.controller;

import java.security.Principal;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootstrap.study.groupware.dto.ScheduleDTO;
import com.bootstrap.study.groupware.entity.Schedule;
import com.bootstrap.study.groupware.service.ScheduleService;
import com.bootstrap.study.util.HolidayDTO;
import com.bootstrap.study.util.HolidayService;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/schedule")
@Log4j2
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final HolidayService holidayService;
    
    public ScheduleController(HolidayService holidayService, ScheduleService scheduleService) {
        this.holidayService = holidayService;
        this.scheduleService = scheduleService;
    }
    
    @GetMapping("/holidays")
    @ResponseBody
    public List<Map<String, Object>> getHolidaysForCalendar(@RequestParam("year") int year, @RequestParam("month") int month) {
        List<HolidayDTO> holidays = holidayService.getHolidays(year, month);
        
        List<Map<String, Object>> calendarEvents = new ArrayList<>();
        for (HolidayDTO holiday : holidays) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", holiday.getDateName());
            event.put("start", holiday.getLocdate().replaceAll("(\\d{4})(\\d{2})(\\d{2})", "$1-$2-$3")); // 날짜 형식 변환
            event.put("allDay", true);
            event.put("color", "red"); // 빨간색으로 표시
            calendarEvents.add(event);
        }
        return calendarEvents;
    }

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
    
 // 일정 수정 
    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateSchedule(@RequestBody Schedule schedule) {
        log.info("ScheduleController updateSchedule()", schedule);
        scheduleService.updateSchedule(schedule);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "일정이 성공적으로 수정되었습니다.");
        return response;
    }

    // 일정 삭제 
    @PostMapping("/delete/{schId}")
    @ResponseBody
    public Map<String, Object> deleteSchedule(@PathVariable("schId") Long schId) {
        log.info("ScheduleController deleteSchedule() for ID: {}", schId);
        scheduleService.deleteSchedule(schId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "일정이 성공적으로 삭제되었습니다.");
        return response;
    }
}