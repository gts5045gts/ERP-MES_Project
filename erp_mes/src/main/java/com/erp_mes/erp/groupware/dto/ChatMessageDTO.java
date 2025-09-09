package com.erp_mes.erp.groupware.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
	
    private Long id; 
    private Long roomId; 
    private String senderId;
    private String senderName; 
    private String receiverId; 
    private String content;
    private String type; 
    private LocalDateTime createdAt;
    private boolean readStatus;
}