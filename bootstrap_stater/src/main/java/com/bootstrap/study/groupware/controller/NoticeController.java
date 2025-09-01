package com.bootstrap.study.groupware.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bootstrap.study.commonCode.entity.CommonDetailCode;
import com.bootstrap.study.commonCode.service.CommonCodeService;
import com.bootstrap.study.groupware.dto.NoticeDTO;
import com.bootstrap.study.groupware.entity.Notice;
import com.bootstrap.study.groupware.repository.NoticeRepository;
import com.bootstrap.study.groupware.service.NoticeService;
import com.bootstrap.study.personnel.dto.PersonnelLoginDTO;
import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.repository.PersonnelRepository;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/notice")
@Log4j2
public class NoticeController {

	@Autowired
	private NoticeRepository noticeRepository;
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private PersonnelRepository personnelRepository;
	@Autowired
	private CommonCodeService commonCodeService;

	// 공지사항 목록 페이지를 보여주는 메서드
	@GetMapping("")
	public String noticeList(Model model) {
		log.info("NoticeController noticeList()");

		// 1. 전체 공지사항을 조회하여 모델에 추가 (기존 코드와 동일)
		List<Notice> notices = noticeRepository.findAll();
		model.addAttribute("notices", notices);

		// 2. 현재 로그인한 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentUserId = null; // ✅ 현재 로그인 사용자의 ID를 담을 변수
	    String currentUsername = null; // ⭐현재 로그인 사용자의 username

		// 사용자 정보에서 부서 ID를 담을 변수
		String empDeptId = null;
		String empDeptName = null;

		// 사용자가 로그인되어 있고, UserDetails 객체가 PersonnelLoginDTO 타입인지 확인
		if (authentication != null && authentication.getPrincipal() instanceof PersonnelLoginDTO) {
			PersonnelLoginDTO personnelLoginDTO = (PersonnelLoginDTO) authentication.getPrincipal();
			currentUserId = personnelLoginDTO.getEmpId();
			currentUsername = personnelLoginDTO.getUsername();
			empDeptId = personnelLoginDTO.getEmpDeptId();
			// 부서 ID로 부서명을 조회하는 로직
			// 이전에 CommonCodeService에 추가한 메서드를 활용해야 합니다.
			if (commonCodeService != null) {
				CommonDetailCode deptCode = commonCodeService.getCommonDetailCode(empDeptId);
				if (deptCode != null) {
					empDeptName = deptCode.getComDtNm(); // ✅ 부서명 변수에 값 할당
				}
			}
			log.info("로그인한 사용자의 부서 ID: " + empDeptId);
		}

		// 3. 로그인한 사용자의 부서 ID로 부서별 공지사항을 조회
		List<Notice> deptNotices = new ArrayList<Notice>();
		if (empDeptName != null) {
			// "부서별" 타입의 공지사항 중, 현재 사용자의 부서ID와 일치하는 목록을 조회
			deptNotices = noticeRepository.findByEmpDeptIdAndNotType(empDeptId,empDeptName);
		}
		model.addAttribute("deptNotices", deptNotices);
		model.addAttribute("empDeptName", empDeptName);
		model.addAttribute("currentUserId", currentUserId);
		model.addAttribute("currentUsername", currentUsername);

		return "gw/notice";
	}

	// 공지사항 등록 페이지를 보여주는 메서드
	@GetMapping("/ntcWrite")
	public String ntcWrite(Model model) {
		log.info("NoticeController ntcWrite()");

		// 1. 로그인한 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String empId = null;
	    String empName = null;
	    String empDeptName = null;
	    String empLevelId = null;
	    boolean isAdmin = false;

		if (authentication != null && authentication.getPrincipal() instanceof PersonnelLoginDTO) {
			PersonnelLoginDTO userDetails = (PersonnelLoginDTO) authentication.getPrincipal();
			empId = userDetails.getEmpId();
			empName = userDetails.getName();
			empLevelId = userDetails.getEmpLevelId();
			isAdmin = "AUT001".equals(empLevelId);
			empDeptName = userDetails.getEmpDeptName();
		}
		// 드롭다운에 표시할 공지 유형 목록 생성
	    List<String> noticeTypes = new ArrayList<>();
	    
	    if (isAdmin) { 
	        noticeTypes.add("전체공지");
	        List<CommonDetailCode> allDepartments = commonCodeService.findByComId("DEP");
	        for (CommonDetailCode dept : allDepartments) {
	            noticeTypes.add(dept.getComDtNm());
	        }
	    } else if (empDeptName != null) {
	        noticeTypes.add(empDeptName);
	    }
	    
	    // 모델에 데이터 추가 
	    NoticeDTO noticeDTO = new NoticeDTO();
	    noticeDTO.setEmpId(empName);
	    
	    model.addAttribute("noticeDTO", noticeDTO);
	    model.addAttribute("empName", empName);
	    model.addAttribute("departments", commonCodeService.findByComId("DEP"));
	    model.addAttribute("isAdmin", "AUT001".equals(empLevelId));

	    if (!isAdmin) {
	        model.addAttribute("empDeptName", empDeptName);
	    } else {
	        model.addAttribute("noticeTypes", noticeTypes);
	    }
	    return "gw/ntcWrite";
	}

	// 공지사항 등록 폼에서 데이터가 제출되면 호출되는 메서드
	@PostMapping("/save")
	public String saveNotice(NoticeDTO noticeDTO) {
		log.info("NoticeController saveNotice()");

		// 1. 현재 로그인한 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		PersonnelLoginDTO personnelLoginDTO = null;
		if (authentication != null && authentication.getPrincipal() instanceof PersonnelLoginDTO) {
			personnelLoginDTO = (PersonnelLoginDTO) authentication.getPrincipal();
		}

		Notice notice = new Notice();

		// 2. 로그인한 사용자의 ID로 Personnel 엔티티를 조회하여 Notice에 설정
		if (personnelLoginDTO != null) {
			// findById를 통해 로그인한 사용자 엔티티를 찾아서 notice에 저장
			// PersonnelRepository의 findById는 String을 받음
			Personnel personnel = personnelRepository.findById(personnelLoginDTO.getEmpId()).orElse(null);
			if (personnel != null) {
				// Notice 엔티티는 Personnel 객체를 가짐
				notice.setEmployee(personnel);
			}
		}

		// 3. NoticeDTO의 다른 필드를 Notice 엔티티에 설정
		notice.setNotTitle(noticeDTO.getNotTitle());
		notice.setNotContent(noticeDTO.getNotContent());
		notice.setNotType(noticeDTO.getNotType());
		notice.setCreateAt(new Date());
		notice.setUpdateAt(new Date());

		noticeRepository.save(notice);

		return "redirect:/notice";
	}

	// 공지 수정
	@PostMapping("/ntcUpdate")
	public String updateNotice(Notice notice) {
		log.info("NoticeController updateNotice() called with Notice: {}", notice);
		// noticeService의 updateNotice 메서드를 호출하여 데이터베이스 업데이트를 요청합니다.
		noticeService.updateNotice(notice);
		return "redirect:/notice"; // 수정 후 공지사항 목록으로 리다이렉트
	}

	// (GET) 공지사항 삭제 처리
	@GetMapping("/ntcDelete")
	public String deleteNotice(@RequestParam("id") long id, RedirectAttributes redirectAttributes) {
		noticeService.deleteNoticeById(id); // 데이터베이스에서 삭제
		redirectAttributes.addFlashAttribute("message", "공지사항이 삭제되었습니다.");
		return "redirect:/notice"; // 목록 페이지로 리다이렉트
	}
}