package com.erp_mes.erp.groupware.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.service.CommonCodeService;
import com.erp_mes.erp.config.util.HolidayDTO;
import com.erp_mes.erp.config.util.HolidayService;
import com.erp_mes.erp.groupware.dto.ScheduleDTO;
import com.erp_mes.erp.groupware.entity.Schedule;
import com.erp_mes.erp.groupware.service.ScheduleService;
import com.erp_mes.erp.personnel.dto.PersonnelLoginDTO;
import com.erp_mes.erp.personnel.entity.Personnel;
import com.erp_mes.erp.personnel.repository.PersonnelRepository;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/schedule")
@Log4j2
public class ScheduleController {

	private final ScheduleService scheduleService;
	private final HolidayService holidayService;
	private final CommonCodeService commonCodeService;
	private final PersonnelRepository personnelRepository;

	public ScheduleController(ScheduleService scheduleService, HolidayService holidayService,
			CommonCodeService commonCodeService, PersonnelRepository personnelRepository) {
		this.scheduleService = scheduleService;
		this.holidayService = holidayService;
		this.commonCodeService = commonCodeService;
		this.personnelRepository = personnelRepository;
	}

	@GetMapping("/holidays")
	@ResponseBody
	public List<Map<String, Object>> getHolidaysForCalendar(
			@RequestParam(value = "year", required = false) Integer year, // required = false 추가
			@RequestParam(value = "month", required = false) Integer month) { // required = false 추가

		int currentYear = (year != null) ? year : java.time.YearMonth.now().getYear();
		int currentMonth = (month != null) ? month : java.time.YearMonth.now().getMonthValue();

		// year 또는 month가 null이면 현재 연도/월로 기본값 설정
		if (year == null || month == null) {
			// 기본값 로직을 여기에 추가
		}

		List<HolidayDTO> holidays = holidayService.getHolidays(currentYear, currentMonth);
		holidays.addAll(holidayService.getHolidays(currentYear, currentMonth + 1));
		holidays.addAll(holidayService.getHolidays(currentYear, currentMonth - 1));

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
	public String scheduleList(Model model, @AuthenticationPrincipal PersonnelLoginDTO personnelLoginDTO) {

		String empDeptName = null;
		boolean isAdmin = false;

		String empDeptId = personnelLoginDTO.getEmpDeptId();
		if (commonCodeService != null) {
			CommonDetailCode deptCode = commonCodeService.getCommonDetailCode(empDeptId);
			if (deptCode != null) {
				empDeptName = deptCode.getComDtNm();
			}
		}

		if (personnelLoginDTO.getEmpLevelId().equals("AUT001")) {
			isAdmin = true;
			List<CommonDetailCode> allDepartments = commonCodeService.findByComId("DEP");
			model.addAttribute("allDepartments", allDepartments);
		}

		model.addAttribute("currentEmpId", personnelLoginDTO.getEmpId());
		model.addAttribute("currentEmpName", personnelLoginDTO.getName());
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("empDeptName", empDeptName);
		model.addAttribute("empDeptId", empDeptId);
		model.addAttribute("empName", personnelLoginDTO.getName());

		return "gw/schedule";

	}

	@GetMapping("/schWrite")
	public String writeForm(Model model, @AuthenticationPrincipal PersonnelLoginDTO personnelLoginDTO) {

		String empDeptName = null;
		boolean isAdmin = personnelLoginDTO.getEmpLevelId().equals("AUT001");

		String empDeptId = personnelLoginDTO.getEmpDeptId();
		if (commonCodeService != null) {
			CommonDetailCode deptCode = commonCodeService.getCommonDetailCode(empDeptId);
			if (deptCode != null) {
				empDeptName = deptCode.getComDtNm();
			}
		}

		if (isAdmin) {
			List<CommonDetailCode> allDepartments = commonCodeService.findByComId("DEP");
			model.addAttribute("allDepartments", allDepartments);
		}

		model.addAttribute("empName", personnelLoginDTO.getName());
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("empDeptName", empDeptName);
		model.addAttribute("currentEmpId", personnelLoginDTO.getEmpId());

		return "gw/schWrite";

	}

	@PostMapping("/save")
	@ResponseBody
	public Map<String, Object> saveSchedule(@RequestBody ScheduleDTO scheduleDTO, Principal principal) {

		String empIdString = principal.getName();
		Personnel currentEmp = personnelRepository.findById(empIdString)
				.orElseThrow(() -> new RuntimeException("직원 정보를 찾을 수 없습니다."));

		Schedule schedule = new Schedule();
		schedule.setEmployee(currentEmp);
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

		List<Schedule> schedules = scheduleService.findAllSchedules();
		List<Map<String, Object>> events = schedules.stream()
					.map(schedule -> {
							Map<String, Object> event = new HashMap<>();
							event.put("id", schedule.getSchId());
							event.put("title", schedule.getSchTitle());
							event.put("start", schedule.getStarttimeAt());
							event.put("end", schedule.getEndtimeAt());
							event.put("schType", schedule.getSchType());
							event.put("empId", schedule.getEmployee().getEmpId());
							return event;
					})
					.collect(Collectors.toList());
		return events;
	}

	@GetMapping("/events/dept")
	@ResponseBody
	public List<Map<String, Object>> getDeptSchedules(
			@RequestParam(value = "empDeptName", required = false) String empDeptName) { // required = false 추가

		List<Schedule> schedules;
		if (empDeptName == null || empDeptName.isEmpty()) {
			schedules = scheduleService.findAllSchedules(); // 부서 이름이 없으면 전체 일정 가져오기
		} else {
			schedules = scheduleService.findByEmpDeptName(empDeptName);
		}
		List<Map<String, Object>> events = schedules.stream()
						.map(schedule -> {
								Map<String, Object> event = new HashMap<>();
								event.put("id", schedule.getSchId());
								event.put("title", schedule.getSchTitle());
								event.put("start", schedule.getStarttimeAt());
								event.put("end", schedule.getEndtimeAt());
								event.put("schType", schedule.getSchType());
								event.put("empId", schedule.getEmployee().getEmpId());
								event.put("empName", schedule.getEmployee().getName());
								return event;
						})
						.collect(Collectors.toList());
		return events;
	}

	@GetMapping("/{schId}")
	@ResponseBody
	public Map<String, Object> getScheduleDetail(@PathVariable("schId") Long schId) {
		Schedule schedule = scheduleService.findById(schId);
		Map<String, Object> result = new HashMap<>();

		if (schedule != null) {

			ScheduleDTO scheduleDTO = new ScheduleDTO(schedule);

			result.put("success", true);
			result.put("schedule", scheduleDTO);

			// 1. schedule 객체에서 empId 가져오기
			String empId = schedule.getEmployee().getEmpId();

			// 2. empId를 이용해 empName을 조회하는 서비스 메서드 호출
			String empName = scheduleService.getEmpNameById(empId);

			// 3. 응답 맵에 empName 추가
			result.put("empName", empName);

		} else {
			result.put("success", false);
			result.put("message", "일정 정보를 찾을 수 없습니다.");
		}
		return result;
	}

	@PostMapping("/update")
	@ResponseBody
	public Map<String, Object> updateSchedule(@RequestBody ScheduleDTO scheduleDTO, Principal principal) {
		Map<String, Object> response = new HashMap<>();

		if (!scheduleService.isScheduleOwner(scheduleDTO.getSchId(), principal.getName())) {
			response.put("success", false);
			response.put("message", "수정 권한이 없습니다.");
			return response;
		}

		scheduleService.updateSchedule(scheduleDTO);
		response.put("success", true);
		response.put("message", "일정이 성공적으로 수정되었습니다.");
		return response;
	}

	@PostMapping("/delete/{schId}")
	@ResponseBody
	public Map<String, Object> deleteSchedule(@PathVariable("schId") Long schId, Principal principal) {
		Map<String, Object> response = new HashMap<>();

		if (!scheduleService.isScheduleOwner(schId, principal.getName())) {
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