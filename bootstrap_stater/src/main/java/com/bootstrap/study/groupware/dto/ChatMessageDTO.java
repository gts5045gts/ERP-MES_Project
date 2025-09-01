package com.bootstrap.study.groupware.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
	
	public enum MessageType{
		JOIN, CHAT, LEAVE
	}
	
	private MessageType type;
	private String chatRoomId;
	private String senderId;
	private String senderName;
	private String content;

}
