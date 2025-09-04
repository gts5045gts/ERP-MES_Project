package com.bootstrap.study.groupware.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

	private final NoticeRepository noticeRepository;
	private final NoticeService noticeService;
	private final PersonnelRepository personnelRepository;
	private final CommonCodeService commonCodeService;

	public NoticeController(NoticeRepository noticeRepository, NoticeService noticeService,
			PersonnelRepository personnelRepository, CommonCodeService commonCodeService) {
		super();
		this.noticeRepository = noticeRepository;
		this.noticeService = noticeService;
		this.personnelRepository = personnelRepository;
		this.commonCodeService = commonCodeService;
	}

	// 공지사항 목록 페이지를 보여주는 메서드
	@GetMapping("")
	public String noticeList(Model model, @AuthenticationPrincipal PersonnelLoginDTO personnelLoginDTO) {
		log.info("NoticeController noticeList()");

		// 1. 전체 공지사항을 조회하여 모델에 추가 (기존 코드와 동일)
		List<Notice> notices = noticeRepository.findAll();
		model.addAttribute("notices", notices);

		// 2. 현재 로그인한 사용자 정보 가져오기
		String empDeptName = null;

		if (personnelLoginDTO != null) {
			// 부서 ID로 부서명 조회
			if (commonCodeService != null) {
				CommonDetailCode deptCode = commonCodeService.getCommonDetailCode(personnelLoginDTO.getEmpDeptId());
				if (deptCode != null) {
					empDeptName = deptCode.getComDtNm();
				}
			}
			log.info("로그인한 사용자의 부서 ID: " + personnelLoginDTO.getEmpDeptId());
		}

		// 3. 로그인한 사용자의 부서 ID로 부서별 공지사항을 조회
		List<Notice> deptNotices = new ArrayList<Notice>();
		if (empDeptName != null) {
			// "부서별" 타입의 공지사항 중, 현재 사용자의 부서ID와 일치하는 목록을 조회
			deptNotices = noticeRepository.findByEmpDeptIdAndNotType(personnelLoginDTO.getEmpDeptId(), empDeptName);
		}
		model.addAttribute("deptNotices", deptNotices);
		model.addAttribute("empDeptName", empDeptName);
		model.addAttribute("currentUserId", personnelLoginDTO.getEmpId());
		model.addAttribute("currentUsername", personnelLoginDTO.getName());

		return "gw/notice";
	}

	// 공지사항 등록 페이지를 보여주는 메서드
	@GetMapping("/ntcWrite")
	public String ntcWrite(Model model, @AuthenticationPrincipal PersonnelLoginDTO personnelLoginDTO) {
		log.info("NoticeController ntcWrite()");

		boolean isAdmin = false;
		String empDeptName = null;

		if (personnelLoginDTO != null) {
			isAdmin = "AUT001".equals(personnelLoginDTO.getEmpLevelId());
			empDeptName = personnelLoginDTO.getEmpDeptName();
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
		noticeDTO.setEmpId(personnelLoginDTO.getName());

		model.addAttribute("noticeDTO", noticeDTO);
		model.addAttribute("empName", personnelLoginDTO.getName());
		model.addAttribute("departments", commonCodeService.findByComId("DEP"));
		model.addAttribute("isAdmin", isAdmin);

		if (!isAdmin) {
			model.addAttribute("empDeptName", empDeptName);
		} else {
			model.addAttribute("noticeTypes", noticeTypes);
		}
		return "gw/ntcWrite";
	}

	// 공지사항 등록 폼에서 데이터가 제출되면 호출되는 메서드
	@PostMapping("/save")
	public String saveNotice(NoticeDTO noticeDTO, @AuthenticationPrincipal PersonnelLoginDTO personnelLoginDTO) {
		log.info("NoticeController saveNotice()");

		Notice notice = new Notice();

		// 로그인한 사용자의 ID로 Personnel 엔티티를 조회하여 Notice에 설정
		if (personnelLoginDTO != null) {
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
	@ResponseBody
	public String updateNotice(@RequestBody Notice notice) {
		log.info("NoticeController updateNotice() called with Notice: {}", notice);
		// noticeService의 updateNotice 메서드를 호출하여 데이터베이스 업데이트를 요청합니다.
		noticeService.updateNotice(notice);
		return "success"; // 수정 후 공지사항 목록으로 리다이렉트
	}

	// (GET) 공지사항 삭제 처리
	@GetMapping("/ntcDelete")
	public String deleteNotice(@RequestParam("id") long id, RedirectAttributes redirectAttributes) {
		noticeService.deleteNoticeById(id); // 데이터베이스에서 삭제
		redirectAttributes.addFlashAttribute("message", "공지사항이 삭제되었습니다.");
		return "redirect:/notice"; // 목록 페이지로 리다이렉트
	}
}