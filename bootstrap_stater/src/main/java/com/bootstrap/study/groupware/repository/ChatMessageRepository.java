package com.bootstrap.study.groupware.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bootstrap.study.groupware.entity.ChatMessage;
import com.bootstrap.study.groupware.entity.ChatRoom;
import com.bootstrap.study.personnel.entity.Personnel;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // ⭐️ 채팅방 엔티티를 활용하여 메시지를 조회
    List<ChatMessage> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);
    
    // 특정 사원에게 온 읽지 않은 메시지를 조회
    List<ChatMessage> findByReceiverAndReadStatus(Personnel receiver, boolean readStatus);
}