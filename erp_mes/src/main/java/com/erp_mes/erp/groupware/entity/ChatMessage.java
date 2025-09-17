package com.erp_mes.erp.groupware.entity;

import java.time.LocalDateTime;

import com.erp_mes.erp.personnel.entity.Personnel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 채팅방 엔티티를 참조하는 관계
    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "emp_id")
    private Personnel sender;

    // name 속성을 'receiver_id'로 수정
    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "emp_id")
    private Personnel receiver;
    
    @Column(name="content", columnDefinition ="CLOB")
    private String content;
    private String type;
    private LocalDateTime createdAt;
    private boolean readStatus;
}