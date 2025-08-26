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
	private NoticeService noticeService;
	private PersonnelRepository personnelRepository;
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

		// 사용자 정보에서 부서 ID를 담을 변수
		String empDeptId = null;

		// 사용자가 로그인되어 있고, UserDetails 객체가 PersonnelLoginDTO 타입인지 확인
		if (authentication != null && authentication.getPrincipal() instanceof PersonnelLoginDTO) {
			PersonnelLoginDTO personnelLoginDTO = (PersonnelLoginDTO) authentication.getPrincipal();
			empDeptId = personnelLoginDTO.getEmpDeptId();
			log.info("로그인한 사용자의 부서 ID: " + empDeptId);
		}

		// 3. 로그인한 사용자의 부서 ID로 부서별 공지사항을 조회
		List<Notice> deptNotices = new ArrayList<Notice>();
		if (empDeptId != null) {
			// "부서별" 타입의 공지사항 중, 현재 사용자의 부서ID와 일치하는 목록을 조회
			deptNotices = noticeRepository.findByEmpDeptIdAndNotType(empDeptId, "부서별");
		}
		model.addAttribute("deptNotices", deptNotices);

		return "gw/notice";
	}

	// 공지사항 등록 페이지를 보여주는 메서드
	@GetMapping("/ntcWrite")
	public String ntcWrite(Model model) {
		log.info("NoticeController ntcWrite()");
		model.addAttribute("noticeDTO", new NoticeDTO());
		return "gw/ntcWrite"; // ✅ ntcWrite.html 파일을 반환
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
			Personnel personnel = personnelRepository.findById(personnelLoginDTO.getEmpId()).orElse(null);
			if (personnel != null) {
				notice.setEmployee(personnel);
			}
		}

		notice.setNotTitle(noticeDTO.getNotTitle());
		notice.setNotContent(noticeDTO.getNotContent());
		notice.setNotType(noticeDTO.getNotType());
		notice.setCreateAt(new Date());
		notice.setUpdateAt(new Date());

		noticeRepository.save(notice);

		// 저장 후 공지사항 목록 페이지로 리다이렉트
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