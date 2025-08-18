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

import com.bootstrap.study.groupware.dto.NoticeDTO;
import com.bootstrap.study.groupware.entity.Notice;
import com.bootstrap.study.groupware.repository.NoticeRepository;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeRepository noticeRepository;

    // 공지사항 목록 페이지를 보여주는 메서드
    // URL: http://localhost:8080/notice
    @GetMapping({"", "/"})
    public String notice(Model model) {
        List<Notice> notices = noticeRepository.findAll();
        model.addAttribute("notices", notices);
        return "gw/notice"; // ✅ notice.html 파일을 반환
    }

    // 공지사항 등록 페이지를 보여주는 메서드
    // URL: http://localhost:8080/notice/write
    @GetMapping("/ntcWrite")
    public String ntcWrite(Model model) {
        model.addAttribute("noticeDTO", new NoticeDTO());
        return "gw/ntcWrite"; // ✅ ntcWrite.html 파일을 반환
    }

    // 공지사항 등록 폼에서 데이터가 제출되면 호출되는 메서드
    // URL: http://localhost:8080/notice/save
    @PostMapping("/save")
    public String saveNotice(NoticeDTO noticeDTO) {
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
}