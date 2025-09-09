package com.erp_mes.erp.config.defaultController;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.service.CommonCodeService;
import com.erp_mes.erp.config.util.HolidayService;
import com.erp_mes.erp.groupware.service.ScheduleService;
import com.erp_mes.erp.personnel.dto.PersonnelLoginDTO;
import com.erp_mes.erp.personnel.repository.PersonnelRepository;

@Controller
public class WebController {
	
	private final ScheduleService scheduleService;
	private final HolidayService holidayService;
	private final CommonCodeService commonCodeService;
	private final PersonnelRepository personnelRepository;

	public WebController(ScheduleService scheduleService, HolidayService holidayService,
			CommonCodeService commonCodeService, PersonnelRepository personnelRepository) {
		this.scheduleService = scheduleService;
		this.holidayService = holidayService;
		this.commonCodeService = commonCodeService;
		this.personnelRepository = personnelRepository;
	}
	@RequestMapping("/")
	public String login(@CookieValue(value = "remember-id", required = false) String rememberId, Model model) {
		// 쿠키값 Model 객체에 추가
		model.addAttribute("rememberId", rememberId);
		model.addAttribute("rememberChecked", true);
		return "login";
	}

	@RequestMapping("main")
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

		if ("AUT001".equals(personnelLoginDTO.getEmpLevelId())) {
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

		return "main";
	}

	@RequestMapping("register")
	public String regist() {

		return "register";
	}

	@RequestMapping("blank")
	public String blank() {

		return "blank";
	}

}