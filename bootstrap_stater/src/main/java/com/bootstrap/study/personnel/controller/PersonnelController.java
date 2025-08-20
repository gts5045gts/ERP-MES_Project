package com.bootstrap.study.personnel.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bootstrap.study.personnel.dto.DepartmentDTO;
import com.bootstrap.study.personnel.dto.PersonnelDTO;
import com.bootstrap.study.personnel.dto.PositionDTO;
import com.bootstrap.study.personnel.service.PersonnelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/personnel")
@RequiredArgsConstructor
@Log4j2
public class PersonnelController {

	private final PersonnelService personnelService;

	@GetMapping("/current")
	public String current() {
		log.info("PersonnelController current()");

		return "/hrn/personnelCurrent";
	}

	@GetMapping("/regist")
	public String regist(Model model) {
		log.info("PersonnelController regist()");
		
		List<DepartmentDTO> departments = personnelService.getAllDepartments();
		model.addAttribute("departments", departments);
		
		List<PositionDTO> position = personnelService.getAllPositions();
		model.addAttribute("position", position);
		
		log.info("position" + position.toString());
		log.info("departments" + departments.toString());
		

		return "/hrn/personnelRegist";
	}
	@PostMapping("/registPro")
	public String registPro(PersonnelDTO personnelDTO) {
		log.info("등록할 사원 정보 : " + personnelDTO.toString());
		
		personnelService.personRegist(personnelDTO);
		
		
		
		
		
		
		return "/hrn/personnelCurrent";
	}
	

	@GetMapping("/app")
	public String app() {
		log.info("PersonnelController app()");

		return "/hrn/personnelApp";
	}

	@GetMapping("/orgChart")
	public String showOrgChart(Model model) {
		// 모든 부서 목록을 가져와 모델에 추가
		List<DepartmentDTO> departments = personnelService.getAllDepartments();
		model.addAttribute("departments", departments);

		// 초기 직원 목록을 가져오는 구문
		List<PersonnelDTO> personnels;
		if (!departments.isEmpty()) {
			// 첫 번째 부서의 ID를 가져와 해당 부서의 직원 목록을 조회
			Long firstDeptId = departments.get(0).getId();
			personnels = personnelService.getEmployeesByDepartmentId(firstDeptId);
		} else {
			// 부서가 없을 경우 빈 목록을 추가
			personnels = Collections.emptyList();
		}

		// 모델에 직원 목록을 추가하여 초기 화면에 표시
		model.addAttribute("personnels", personnels);

		return "/hrn/orgchart";
	}

	// AJAX 요청을 처리하여 특정 부서의 직원 정보를 JSON 형태로 반환
	@GetMapping("/employees")
	public ResponseEntity<List<PersonnelDTO>> getPersonnels(@RequestParam("deptId") Long deptId) {
		List<PersonnelDTO> personnels = personnelService.getEmployeesByDepartmentId(deptId);
		log.info("+++++++++++++++++++ employee");

		return ResponseEntity.ok(personnels);
	}

}
