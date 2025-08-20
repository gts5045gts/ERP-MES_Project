package com.bootstrap.study.groupware.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bootstrap.study.groupware.dto.NoticeDTO;
import com.bootstrap.study.groupware.entity.Notice;
import com.bootstrap.study.groupware.repository.NoticeRepository;
import com.bootstrap.study.groupware.service.NoticeService;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/notice")
@Log4j2
public class NoticeController {

    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private NoticeService noticeService;

    // 공지사항 목록 페이지를 보여주는 메서드
    @GetMapping("")
    public String notice(Model model) {
    	log.info("NoticeController notice()");
    	// 1. 전체 공지사항을 조회하여 모델에 추가
        List<Notice> notices = noticeRepository.findAll();
        model.addAttribute("notices", notices);

        // 2. 부서별 공지사항을 조회하여 모델에 추가
        List<Notice> deptNotices = noticeRepository.findByNotType("부서별"); 
        model.addAttribute("deptNotices", deptNotices);
        return "gw/notice"; // ✅ notice.html 파일을 반환
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
        Notice notice = new Notice();
        notice.setEmpId(Long.valueOf(noticeDTO.getEmpId()));
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