package com.bootstrap.study.personnel.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bootstrap.study.commonCode.dto.CommonDetailCodeDTO;
import com.bootstrap.study.personnel.dto.PersonnelDTO;
import com.bootstrap.study.personnel.dto.PersonnelLoginDTO;
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
	public String current(Model model) {
		List<PersonnelDTO> personnels = personnelService.getAllPersonnels();
		model.addAttribute("personnels", personnels);
		
		return "/hrn/personnelCurrent";
	}
	
	// 인사현황 데이터 응답
	@GetMapping("/api/personnels")
    public ResponseEntity<List<PersonnelDTO>> getAllPersonnels() {
        List<PersonnelDTO> personnels = personnelService.getAllPersonnels();
        
        return ResponseEntity.ok(personnels);
    }
	
	@GetMapping("/detailInfo")
    public String detailInfo(@RequestParam("empId") String empId, Model model) {
        // Service를 통해 해당 사원의 상세 정보를 가져와 모델에 추가
        Optional<PersonnelDTO> personnelOpt = personnelService.getPersonnelDetails(empId);
        
        if (personnelOpt.isPresent()) {
            model.addAttribute("personnel", personnelOpt.get());
        } else {
            // 사원 정보가 없을 경우 리스트 페이지로 리다이렉트
            return "redirect:/personnel/current";
        }

        // 부서 및 직책 리스트도 모델에 추가 (select 박스 생성을 위해 필요)
        List<CommonDetailCodeDTO> departments = personnelService.getAllDepartments();
        model.addAttribute("departments", departments);
        log.info("personnel : " + personnelOpt);
        log.info("부서 정보 : " + departments.toString());

        List<CommonDetailCodeDTO> position = personnelService.getAllPositions();
        model.addAttribute("position", position);
        log.info("직책 정보 : " + position.toString());
    	//추가 된 부분 ----------------------------------------------------
    	//재직 리스트
		List<CommonDetailCodeDTO> status = personnelService.getAllStatus();
		model.addAttribute("status", status);
        log.info("상태 정보 : " + status.toString());

		//보안등급
		List<CommonDetailCodeDTO> level = personnelService.getAllLevel();
		model.addAttribute("level", level);
        log.info("보안등급 정보 : " + level.toString());
        
        log.info("사원등급 정보 : " + departments.toString());
        

		//추가 된 부분 ----------------------------------------------------
		

        return "/hrn/personnelDetailInfo";
    }

    // 인사현황 -> 상세조회 버튼 -> 정보 수정시 수행 
    @PostMapping("/updatePro")
    public String updatePro(PersonnelDTO personnelDTO, RedirectAttributes redirectAttributes) {
        log.info("수정할 사원 정보 : " + personnelDTO.toString());
        
        try {
            personnelService.updatePersonnel(personnelDTO);
            redirectAttributes.addFlashAttribute("message", "사원 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "사원 정보 수정에 실패했습니다.");
        }
        
        return "redirect:/personnel/current";
    }
	
	@GetMapping("/regist")
	public String regist(Model model) {
		log.info("PersonnelController regist()");
		
		// 부서 리스트
		List<CommonDetailCodeDTO> departments = personnelService.getAllDepartments();
		model.addAttribute("departments", departments);
		
		// 직책 리스트 
		List<CommonDetailCodeDTO> position = personnelService.getAllPositions();
		model.addAttribute("position", position);
		
		
		//추가 된 부분 ----------------------------------------------------
		
		//재직 리스트 
		List<CommonDetailCodeDTO> status = personnelService.getStatus();
		model.addAttribute("status", status);
		
		//보안등급 리스트
		List<CommonDetailCodeDTO> level = personnelService.getAllLevel();
		model.addAttribute("level", level);
		//추가 된 부분 ----------------------------------------------------
		
		log.info("position" + position.toString());
		log.info("departments" + departments.toString());
		

		return "/hrn/personnelRegist";
	}
	
	@PostMapping("/registPro")
	public String registPro(PersonnelDTO personnelDTO, @RequestParam("empImg") MultipartFile empImg) throws IOException {
		log.info("등록할 사원 정보 : " + personnelDTO.toString());
		
		personnelService.personRegist(personnelDTO, empImg);

		return "/hrn/personnelCurrent";
	}
	
	// 인사발령
	@GetMapping("/trans")
	public String trans(Model model, @AuthenticationPrincipal PersonnelLoginDTO userDetails) {
		log.info("PersonnelController trans()");
		
		// 로그인한 사용자의 부서 정보 확인
		String userDeptId = userDetails.getEmpDeptId(); 

        // 부서 코드가 'DEP001'(인사팀)일 경우, 버튼 표시 여부를 true로 설정
        boolean isHrTeam = "DEP001".equals(userDeptId);
        
        model.addAttribute("isHRTeam", isHrTeam);
		
		return "/hrn/personnelTrans";
	}
	
	// 발령 등록 폼
	@GetMapping("/trans/save")
    public String transSave(Model model) {
        log.info("PersonnelController transSave()");
        
        // 팝업 페이지에 필요한 부서, 직급 리스트 추가
        List<CommonDetailCodeDTO> departments = personnelService.getAllDepartments();
        model.addAttribute("departments", departments);

        List<CommonDetailCodeDTO> position = personnelService.getAllPositions();
        model.addAttribute("position", position);
        
        // 전체 사원 리스트
        List<PersonnelDTO> allEmployees = personnelService.getAllPersonnels();
        model.addAttribute("allEmployees", allEmployees);
        
        // 인사팀 and 중간관리자 or 상위관리자 사원 목록
        List<PersonnelDTO> approvers = personnelService.getEmployeesByDeptIdLevel("DEP001", "AUT001", "AUT002");
        model.addAttribute("approvers", approvers);

        return "/hrn/personnelTransSave";  
    }
	
	@PostMapping("/trans/submit")
    public ResponseEntity<String> transPersonnel(@RequestBody Map<String, Object> payload, Principal principal) {
        log.info("인사 발령 요청 수신: {}", payload);
        String loginEmpId = principal.getName(); // 로그인한 사용자 ID
        try {
            // Service 계층으로 발령 데이터 전달
            personnelService.submitTransPersonnel(payload, loginEmpId);
            return ResponseEntity.ok("인사 발령 신청이 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            log.error("인사 발령 처리 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("인사 발령 처리 실패: " + e.getMessage());
        }
    }

	
	// 현재에 맞게 다시 수정 
	@GetMapping("/orgChart")
    public String showOrgChart(Model model) {
        // 모든 부서 목록을 가져와 모델에 추가
        List<CommonDetailCodeDTO> departments = personnelService.getAllDepartments();
        model.addAttribute("departments", departments);
		
		// 초기 직원 목록을 가져오는 구문
        List<PersonnelDTO> personnels;
        if (!departments.isEmpty()) {
            // 첫 번째 부서의 ID를 가져와 해당 부서의 직원 목록을 조회
            String firstDeptId = departments.get(0).getComDtId();
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
    public ResponseEntity<List<PersonnelDTO>> getPersonnels(@RequestParam("deptId") String comDtId) {
        List<PersonnelDTO> personnels = personnelService.getEmployeesByDepartmentId(comDtId);

        return ResponseEntity.ok(personnels);
    }
    
    

}