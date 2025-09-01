package com.bootstrap.study.groupware.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
	public String chatPage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		// 로그인한 사용자의 정보를 모델에 추가하여 HTML에서 사용하도록 합니다.
		if (authentication != null && authentication.getPrincipal() instanceof PersonnelLoginDTO) {
			PersonnelLoginDTO userDetails = (PersonnelLoginDTO) authentication.getPrincipal();
			model.addAttribute("currentEmpId", userDetails.getEmpId());
			model.addAttribute("currentEmpName", userDetails.getName());
		} else {
			// 로그인하지 않은 경우 처리 (예: 로그인 페이지로 리다이렉트)
			return "redirect:/login";
		}
		
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
    public String privateChatPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof PersonnelLoginDTO) {
            PersonnelLoginDTO userDetails = (PersonnelLoginDTO) authentication.getPrincipal();
            model.addAttribute("currentEmpId", userDetails.getEmpId());
            model.addAttribute("currentEmpName", userDetails.getName());
        } else {
            return "redirect:/login";
        }
        return "gw/privateChat";
    }
    
    // 개인 메시지를 전송하는 메서드 추가
    @MessageMapping("/chat.privateMessage")
    public void privateMessage(@Payload ChatMessageDTO chatMessage, Principal principal) {
    	log.info("개인 메시지 전송: {}", chatMessage);
    	
    	chatService.saveMessage(chatMessage);
    	
    	// 메시지 수신자에게 메시지를 전송
    	// "/queue/private"는 클라이언트가 개인 메시지를 받을 구독 주소
    	messagingTemplate.convertAndSendToUser(
    			chatMessage.getReceiverId(), "/queue/private", chatMessage
    			);
    	
    	// 보낸 사람의 화면에도 메시지를 다시 전송 (선택 사항)
    	messagingTemplate.convertAndSendToUser(
    			principal.getName(), "/queue/private", chatMessage
    			);
    }
    
    // 클라이언트의 '읽지 않은 메시지 불러오기' 요청을 처리합니다.
    @GetMapping("/api/messages/unread")
    @ResponseBody
    public List<ChatMessageDTO> getUnreadMessages(Principal principal) {
        log.info("읽지 않은 메시지 불러오기 요청: {}", principal.getName());
        // Service 계층을 호출하여 데이터베이스에서 읽지 않은 메시지를 가져옵니다.
        // `principal.getName()`은 현재 로그인한 사용자의 ID(Principal ID)를 반환합니다.
        return chatService.getUnreadMessages(principal.getName());
    }
}
