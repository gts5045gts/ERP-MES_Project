package com.bootstrap.study.groupware.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bootstrap.study.groupware.entity.Notice;
import com.bootstrap.study.groupware.repository.NoticeRepository;

@Service
public class NoticeService {
	
	private final NoticeRepository noticeRepository;
	
    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }
    
    // 공지 수정
    public void updateNotice(Notice notice) {
    	Optional<Notice> existingNoticeOptional = noticeRepository.findById(notice.getNotId());
    	if (existingNoticeOptional.isPresent()) {
            Notice existingNotice = existingNoticeOptional.get();

            // 2. 폼에서 받은 데이터로 기존 공지사항의 내용을 업데이트합니다.
            existingNotice.setNotTitle(notice.getNotTitle());
            existingNotice.setNotContent(notice.getNotContent());
            existingNotice.setUpdateAt(new Date()); 
            
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
	
	public List<Notice> getDeptNoticesByDeptId(String empDeptId) {
	    // "부서별" 타입의 공지사항 중, 현재 사용자의 부서ID와 일치하는 목록을 조회
	    return noticeRepository.findByEmpDeptIdAndNotType(empDeptId, "부서별");
	}

}
