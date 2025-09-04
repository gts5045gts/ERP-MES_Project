package com.bootstrap.study.groupware.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bootstrap.study.groupware.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // roomId (String)를 기준으로 ChatRoom을 찾는 메서드
    Optional<ChatRoom> findByRoomId(String roomId);
}