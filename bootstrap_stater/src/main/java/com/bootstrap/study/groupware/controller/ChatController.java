package com.bootstrap.study.groupware.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootstrap.study.groupware.dto.ChatMessageDTO;
import com.bootstrap.study.groupware.service.ChatService;
import com.bootstrap.study.personnel.dto.PersonnelLoginDTO;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class ChatController {

	private final SimpMessageSendingOperations messagingTemplate;
	private final ChatService chatService;

	public ChatController(SimpMessageSendingOperations messagingTemplate, ChatService chatService) {
		super();
		this.messagingTemplate = messagingTemplate;
		this.chatService = chatService;
	}

	@GetMapping("/chat")
	public String chatPage(Model model, @AuthenticationPrincipal PersonnelLoginDTO personnelLoginDTO) {
			model.addAttribute("currentEmpId", personnelLoginDTO.getEmpId());
			model.addAttribute("currentEmpName", personnelLoginDTO.getName());

		return "gw/chat";
	}

	@MessageMapping("/chat.sendMessage")
	public void sendMessage(@Payload ChatMessageDTO chatMessage) {
		messagingTemplate.convertAndSend("/topic/publicChat", chatMessage);
		log.info("메시지 전송: {}", chatMessage);
	}

	@MessageMapping("/chat.addUser")
	public void addUser(@Payload ChatMessageDTO chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		log.info("사용자 입장: {}", chatMessage);
		// 웹소켓 세션에 사용자 ID와 이름을 추가
		headerAccessor.getSessionAttributes().put("userId", chatMessage.getSenderId());
		headerAccessor.getSessionAttributes().put("userName", chatMessage.getSenderName());
		messagingTemplate.convertAndSend("/topic/publicChat", chatMessage);
	}

	// 1:1 메신저
	@GetMapping("/privateChat")
	public String privateChatPage(Model model, @AuthenticationPrincipal PersonnelLoginDTO personnelLoginDTO) {
			model.addAttribute("currentEmpId", personnelLoginDTO.getEmpId());
			model.addAttribute("currentEmpName", personnelLoginDTO.getName());
			
		return "gw/privateChat";
	}

	// 개인 메시지를 전송하는 메서드 추가
	@MessageMapping("/chat.privateMessage")
	public void privateMessage(@Payload ChatMessageDTO chatMessage, Principal principal) {
	    // 메시지 저장 로직
	    chatService.saveMessage(chatMessage);
	    
	    // 수신자의 개인 채널로 메시지를 전송 (이 메시지를 프론트엔드에서 알림으로 사용)
	    messagingTemplate.convertAndSendToUser(
	        chatMessage.getReceiverId(), "/queue/private", chatMessage
	    );
	    
	    // 보낸 사람의 화면에도 메시지 전송
	    messagingTemplate.convertAndSendToUser(
	        principal.getName(), "/queue/private", chatMessage
	    );
	}

	// 클라이언트의 '읽지 않은 메시지 불러오기' 요청을 처리합니다.
	@GetMapping("/api/messages/unread")
	@ResponseBody
	public List<ChatMessageDTO> getUnreadMessages(Principal principal) {
		log.info("읽지 않은 메시지 불러오기 요청: {}", principal.getName());
		return chatService.getUnreadMessages(principal.getName());
	}
}
