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
import com.bootstrap.study.groupware.dto.ScheduleDTO;
import com.bootstrap.study.groupware.entity.Schedule;
import com.bootstrap.study.groupware.service.ScheduleService;
import com.bootstrap.study.personnel.dto.PersonnelLoginDTO;
import com.bootstrap.study.util.HolidayDTO;
import com.bootstrap.study.util.HolidayService;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/schedule")
@Log4j2
public class ScheduleController {

	private final ScheduleService scheduleService;
	private final HolidayService holidayService;
	private final CommonCodeService commonCodeService;

	public ScheduleController(HolidayService holidayService, ScheduleService scheduleService,
			CommonCodeService commonCodeService) {
		this.holidayService = holidayService;
		this.scheduleService = scheduleService;
		this.commonCodeService = commonCodeService;
	}

	@GetMapping("/holidays")
	@ResponseBody
	public List<Map<String, Object>> getHolidaysForCalendar(@RequestParam("year") int year,
			@RequestParam("month") int month) {
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

			// ⭐ 사용자 이름이 "관리자"인지 확인하는 로직으로 변경
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

		return "gw/schedule";
	}

	// 일정 등록 페이지를 보여주는 메서드 (schWrite.html)
	@GetMapping("/schWrite")
	public String writeForm(Model model) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    
	    // ⭐ 모델에 전달할 변수들
	    String empName = null;
	    boolean isAdmin = false;
	    List<CommonDetailCode> allDepartments = new ArrayList<>();
	    String empDeptName = null;
	    Long currentEmpId = null;

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
	    
	    // ⭐ 모델에 데이터 추가
	    model.addAttribute("empName", empName);
	    model.addAttribute("isAdmin", isAdmin);
	    model.addAttribute("allDepartments", allDepartments);
	    model.addAttribute("empDeptName", empDeptName);
	    model.addAttribute("currentEmpId", currentEmpId);

	    return "gw/schWrite";
	}

	// ⭐ 일정 등록 (모달 & 페이지 폼 제출)을 처리하는 메서드
	@PostMapping("/save")
	@ResponseBody
	public Map<String, Object> saveSchedule(@RequestBody ScheduleDTO scheduleDTO, Principal principal) {
		log.info("ScheduleController saveSchedule()", scheduleDTO);

		// 1. Principal 객체를 사용하여 현재 로그인한 사용자의 empId를 가져옵니다.
		// Principal.getName()은 Spring Security에서 사용자 ID(username)를 반환합니다.
		String empIdString = principal.getName();
		Long currentEmpId = Long.parseLong(empIdString);

		Schedule schedule = new Schedule();

		// 이 과정에서 임시 ID와 타입을 할당합니다.
		schedule.setEmpId(currentEmpId);
		schedule.setSchTitle(scheduleDTO.getSchTitle());
		schedule.setSchContent(scheduleDTO.getSchContent());
		schedule.setStarttimeAt(scheduleDTO.getStarttimeAt());
		schedule.setEndtimeAt(scheduleDTO.getEndtimeAt());

		if (scheduleDTO.getSchType() != null && !scheduleDTO.getSchType().isEmpty()) {
			schedule.setSchType(scheduleDTO.getSchType());
		} else {
			// DTO에 schType이 없으면 기본값으로 사용자의 부서명을 할당
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.getPrincipal() instanceof PersonnelLoginDTO) {
				PersonnelLoginDTO personnelLoginDTO = (PersonnelLoginDTO) authentication.getPrincipal();
				String empDeptName = commonCodeService.getCommonDetailCode(personnelLoginDTO.getEmpDeptId())
						.getComDtNm();
				schedule.setSchType(empDeptName);
			} else {
				schedule.setSchType("부서"); // fallback
			}
		}

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
		List<Map<String, Object>> events = schedules.stream().map(schedule -> {
			Map<String, Object> event = new HashMap<>();
			event.put("id", schedule.getSchId());
			event.put("title", schedule.getSchTitle());
			event.put("start", schedule.getStarttimeAt());
			event.put("end", schedule.getEndtimeAt());
			event.put("schType", schedule.getSchType());
			event.put("empId", schedule.getEmpId());
			// 추가 필드가 있다면 여기에 포함
			return event;
		}).collect(Collectors.toList());
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
		List<Map<String, Object>> events = schedules.stream().map(schedule -> {
			Map<String, Object> event = new HashMap<>();
			event.put("id", schedule.getSchId());
			event.put("title", schedule.getSchTitle());
			event.put("start", schedule.getStarttimeAt());
			event.put("end", schedule.getEndtimeAt());
			return event;
		}).collect(Collectors.toList());
		return events;
	}

	// ⭐ 일정 상세 정보를 조회하고 일정 작성자의 ID를 포함하여 반환
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

	// ⭐ 일정 수정 시 권한 확인 로직 추가
	@PostMapping("/update")
	@ResponseBody
	public Map<String, Object> updateSchedule(@RequestBody Schedule schedule, Principal principal) {
		log.info("ScheduleController updateSchedule()", schedule);
		Map<String, Object> response = new HashMap<>();

		// ⭐ 권한 확인
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

	// 일정 삭제 시 권한 확인 로직 추가
	@PostMapping("/delete/{schId}")
	@ResponseBody
	public Map<String, Object> deleteSchedule(@PathVariable("schId") Long schId, Principal principal) {
		log.info("ScheduleController deleteSchedule() for ID: {}", schId);
		Map<String, Object> response = new HashMap<>();

		// ⭐ 권한 확인
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