package com.erp_mes.erp.groupware.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.erp_mes.erp.groupware.entity.Notice;
import com.erp_mes.erp.groupware.repository.NoticeRepository;

@Service
public class NoticeService {
	
	private final NoticeRepository noticeRepository;
	
    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }
    
 // 로그인 사용자가 공지 작성자인지 확인하는 메서드
    public boolean isAuthor(String currentUserId, Long noticeId) {
        Optional<Notice> noticeOptional = noticeRepository.findById(noticeId);
        if (noticeOptional.isPresent()) {
            Notice notice = noticeOptional.get();
            // 공지사항 작성자의 empId와 현재 로그인한 사용자의 ID를 비교
            return notice.getEmployee().getEmpId().equals(currentUserId);
        }
        return false;
    }
    
    // 공지 수정
    public void updateNotice(Notice notice) {
    	Optional<Notice> existingNoticeOptional = noticeRepository.findById(notice.getNotId());
    	if (existingNoticeOptional.isPresent()) {
            Notice existingNotice = existingNoticeOptional.get();

            // 2. 폼에서 받은 데이터로 기존 공지사항의 내용을 업데이트합니다.
            existingNotice.setNotTitle(notice.getNotTitle());
            existingNotice.setNotContent(notice.getNotContent());
            existingNotice.setUpdateAt(LocalDate.now()); 
            
            // 3. 업데이트된 객체를 저장합니다.
            // EMP_ID, CREATE_AT 등은 기존 값 그대로 유지됩니다.
            noticeRepository.save(existingNotice);
        } else {
            // 해당 ID의 공지사항이 없는 경우, 예외 처리
            // throw new RuntimeException("Notice not found with id: " + notice.getNotId());
            // 또는 로깅 처리
        }
    }

    // 공지 삭제
	public void deleteNoticeById(long id) {
		
		noticeRepository.deleteById(id);
	}
	

}
