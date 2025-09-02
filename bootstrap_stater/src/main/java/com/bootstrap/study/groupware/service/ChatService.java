package com.bootstrap.study.groupware.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bootstrap.study.groupware.dto.ChatMessageDTO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ChatService {
	
	// private final ChatMessageRepository chatMessageRepository; // JPA Repository 의존성 주입

    // 데이터베이스에 메시지를 저장하는 메서드
    public void saveMessage(ChatMessageDTO chatMessage) {
        // 여기에 메시지를 데이터베이스에 저장하는 실제 로직을 구현해야 합니다.
        // 예: chatMessageRepository.save(chatMessage);
        System.out.println("메시지 저장: " + chatMessage.getContent());
    }

    // 데이터베이스에서 읽지 않은 메시지를 가져오는 메서드
    public List<ChatMessageDTO> getUnreadMessages(String userId) {
        // 여기에 DB 쿼리 로직을 구현해야 합니다.
        // 예: return chatMessageRepository.findByReceiverIdAndReadStatus(userId, false);
        System.out.println("읽지 않은 메시지 불러오기: " + userId);
        // 임시로 빈 리스트를 반환합니다.
        return List.of(); 
    }
}
