package com.erp_mes.erp.groupware.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.erp.groupware.dto.ChatMessageDTO;
import com.erp_mes.erp.groupware.service.ChatService;
import com.erp_mes.erp.personnel.dto.PersonnelLoginDTO;

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
	
	// 모든 읽지 않은 메시지를 '읽음' 상태로 업데이트하는 API
	@PostMapping("/api/messages/read")
	@ResponseBody
	public String markMessagesAsRead(Principal principal) {
	    if (principal == null) {
	        return "Not authenticated";
	    }
	    log.info("읽음 상태 업데이트 요청: {}", principal.getName());
	    chatService.markMessagesAsRead(principal.getName());
	    return "OK";
	}
	
	@GetMapping("/api/chat/messages")
	@ResponseBody
	public List<ChatMessageDTO> getChatHistory(@RequestParam(value = "receiverId") String receiverId, Principal principal) {
	    log.info("대화 기록 불러오기 요청: {} <-> {}", principal.getName(), receiverId);
	    return chatService.getChatHistory(principal.getName(), receiverId);
	}

	// getRecentMessages 메서드 수정
	@GetMapping("/api/chat/recent")
	@ResponseBody
	public List<ChatMessageDTO> getRecentMessages(Principal principal) {
	    log.info("최근 메시지 조회 시작: {}", principal.getName());
	    // Principal.getName()을 사용하도록 수정
	    return chatService.getRecentMessages(principal.getName());
	}
}
