package com.bootstrap.study.groupware.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootstrap.study.commonCode.entity.CommonDetailCode;
import com.bootstrap.study.commonCode.service.CommonCodeService;
import com.bootstrap.study.commonCode.util.HolidayDTO;
import com.bootstrap.study.commonCode.util.HolidayService;
import com.bootstrap.study.groupware.dto.ScheduleDTO;
import com.bootstrap.study.groupware.entity.Schedule;
import com.bootstrap.study.groupware.service.ScheduleService;
import com.bootstrap.study.personnel.dto.PersonnelLoginDTO;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/schedule")
@Log4j2
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final HolidayService holidayService;
    private final CommonCodeService commonCodeService;

    public ScheduleController(HolidayService holidayService, ScheduleService scheduleService, CommonCodeService commonCodeService) {
        this.holidayService = holidayService;
        this.scheduleService = scheduleService;
        this.commonCodeService = commonCodeService;
    }

    @GetMapping("/holidays")
    @ResponseBody
    public List<Map<String, Object>> getHolidaysForCalendar(@RequestParam("year") int year, @RequestParam("month") int month) {
        List<HolidayDTO> holidays = holidayService.getHolidays(year, month);
        
        List<Map<String, Object>> calendarEvents = new ArrayList<>();
        for (HolidayDTO holiday : holidays) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", holiday.getDateName());
            event.put("start", holiday.getLocdate().replaceAll("(\\d{4})(\\d{2})(\\d{2})", "$1-$2-$3"));
            event.put("allDay", true);
            event.put("color", "red");
            calendarEvents.add(event);
        }
        return calendarEvents;
    }

    @GetMapping("")
    public String scheduleList(Model model) {
    	log.info("ScheduleController scheduleList()");
    	
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentEmpId = null;
        String empDeptId = null;
        String empDeptName = null;
        String empName = null; 
        boolean isAdmin = false;
        
        if (authentication != null && authentication.getPrincipal() instanceof PersonnelLoginDTO) {
            PersonnelLoginDTO personnelLoginDTO = (PersonnelLoginDTO) authentication.getPrincipal();
            currentEmpId = Long.parseLong(personnelLoginDTO.getEmpId());
            empName = personnelLoginDTO.getName(); // ⭐ 사용자 이름 가져오기
            empDeptId = personnelLoginDTO.getEmpDeptId();

            if (commonCodeService != null) {
                CommonDetailCode deptCode = commonCodeService.getCommonDetailCode(empDeptId);
                if (deptCode != null) {
                    empDeptName = deptCode.getComDtNm();
                }
            }
            
            if ("관리자".equals(empName)) {
                isAdmin = true;
                List<CommonDetailCode> allDepartments = commonCodeService.findByComId("DEP");
                model.addAttribute("allDepartments", allDepartments);
            }
        }
        
        model.addAttribute("currentEmpId", currentEmpId);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("empDeptName", empDeptName);
        model.addAttribute("empDeptId", empDeptId);
        model.addAttribute("empName", empName); // ⭐ empName을 모델에 추가
        
        return "gw/schedule";
    }

    @GetMapping("/schWrite")
    public String writeForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        String empName = null;
        boolean isAdmin = false;
        String empDeptName = null;
        Long currentEmpId = null;
        List<CommonDetailCode> allDepartments = new ArrayList<>();

        if (authentication != null && authentication.getPrincipal() instanceof PersonnelLoginDTO) {
            PersonnelLoginDTO personnelLoginDTO = (PersonnelLoginDTO) authentication.getPrincipal();
            empName = personnelLoginDTO.getName();
            currentEmpId = Long.parseLong(personnelLoginDTO.getEmpId());
            String empDeptId = personnelLoginDTO.getEmpDeptId();

            if (commonCodeService != null) {
                CommonDetailCode deptCode = commonCodeService.getCommonDetailCode(empDeptId);
                if (deptCode != null) {
                    empDeptName = deptCode.getComDtNm();
                }
            }
            
            if ("관리자".equals(empName)) {
                isAdmin = true;
                allDepartments = commonCodeService.findByComId("DEP");
            }
        }
        
        model.addAttribute("empName", empName);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("allDepartments", allDepartments);
        model.addAttribute("empDeptName", empDeptName);
        model.addAttribute("currentEmpId", currentEmpId);

        return "gw/schWrite";
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> saveSchedule(@RequestBody ScheduleDTO scheduleDTO, Principal principal) {
        log.info("ScheduleController saveSchedule()", scheduleDTO);
        
        String empIdString = principal.getName();
        Long currentEmpId = Long.parseLong(empIdString);
        
        Schedule schedule = new Schedule();
        schedule.setEmpId(currentEmpId);
        schedule.setSchTitle(scheduleDTO.getSchTitle());
        schedule.setSchContent(scheduleDTO.getSchContent());
        schedule.setStarttimeAt(scheduleDTO.getStarttimeAt());
        schedule.setEndtimeAt(scheduleDTO.getEndtimeAt());
        schedule.setSchType(scheduleDTO.getSchType()); 
        
        scheduleService.saveSchedule(schedule);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "일정이 성공적으로 등록되었습니다.");
        return response;
    }
    
    @GetMapping("/events/all")
    @ResponseBody
    public List<Map<String, Object>> getAllSchedules() {
    	log.info("ScheduleController getAllSchedules()");
    	
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
                return event;
            })
            .collect(Collectors.toList());
        return events;
    }

    @GetMapping("/events/dept")
    @ResponseBody
    public List<Map<String, Object>> getDeptSchedules(@RequestParam("empDeptName") String empDeptName) {
        List<Schedule> schedules = scheduleService.findByEmpDeptName(empDeptName);
        List<Map<String, Object>> events = schedules.stream()
            .map(schedule -> {
                Map<String, Object> event = new HashMap<>();
                event.put("id", schedule.getSchId());
                event.put("title", schedule.getSchTitle());
                event.put("start", schedule.getStarttimeAt());
                event.put("end", schedule.getEndtimeAt());
                event.put("empId", schedule.getEmpId());
                return event;
            })
            .collect(Collectors.toList());
        return events;
    }
    
    @GetMapping("/{schId}")
    @ResponseBody
    public Map<String, Object> getScheduleDetail(@PathVariable("schId") Long schId) {
        log.info("Requesting schedule detail for ID: {}", schId);
        Schedule schedule = scheduleService.findById(schId);
        Map<String, Object> result = new HashMap<>();
        
        if (schedule != null) {
        	result.put("success", true);
        	result.put("schedule", schedule);
        } else {
        	result.put("success", false);
        	result.put("message", "일정 정보를 찾을 수 없습니다.");
        }
        return result;
    }

    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateSchedule(@RequestBody Schedule schedule, Principal principal) {
        log.info("ScheduleController updateSchedule()", schedule);
        Map<String, Object> response = new HashMap<>();
        
        if (!scheduleService.isScheduleOwner(schedule.getSchId(), Long.parseLong(principal.getName()))) {
            response.put("success", false);
            response.put("message", "수정 권한이 없습니다.");
            return response;
        }

        scheduleService.updateSchedule(schedule);
        response.put("success", true);
        response.put("message", "일정이 성공적으로 수정되었습니다.");
        return response;
    }

    @PostMapping("/delete/{schId}")
    @ResponseBody
    public Map<String, Object> deleteSchedule(@PathVariable("schId") Long schId, Principal principal) {
        log.info("ScheduleController deleteSchedule() for ID: {}", schId);
        Map<String, Object> response = new HashMap<>();

        if (!scheduleService.isScheduleOwner(schId, Long.parseLong(principal.getName()))) {
            response.put("success", false);
            response.put("message", "삭제 권한이 없습니다.");
            return response;
        }
        
        scheduleService.deleteSchedule(schId);
        response.put("success", true);
        response.put("message", "일정이 성공적으로 삭제되었습니다.");
        return response;
    }
}