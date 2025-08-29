package com.bootstrap.study.groupware.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bootstrap.study.groupware.dto.ChatMessageDTO;
import com.bootstrap.study.personnel.dto.PersonnelLoginDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ChatController {

	private final SimpMessageSendingOperations messagingTemplate;

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
}
