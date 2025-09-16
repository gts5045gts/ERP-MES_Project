package com.erp_mes.erp.groupware.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.erp_mes.erp.groupware.dto.ChatMessageDTO;
import com.erp_mes.erp.groupware.entity.ChatMessage;
import com.erp_mes.erp.groupware.entity.ChatRoom;
import com.erp_mes.erp.groupware.repository.ChatMessageRepository;
import com.erp_mes.erp.groupware.repository.ChatRoomRepository;
import com.erp_mes.erp.personnel.entity.Personnel;
import com.erp_mes.erp.personnel.repository.PersonnelRepository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ChatService {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final PersonnelRepository personnelRepository;

	public ChatService(ChatMessageRepository chatMessageRepository, ChatRoomRepository chatRoomRepository,
			PersonnelRepository personnelRepository) {
		this.chatMessageRepository = chatMessageRepository;
		this.chatRoomRepository = chatRoomRepository;
		this.personnelRepository = personnelRepository;
	}

	// 데이터베이스에 메시지를 저장하는 메서드
	public void saveMessage(ChatMessageDTO chatMessageDTO) {
		log.info("채팅 메시지 저장 시작: {}", chatMessageDTO);

		Personnel sender = personnelRepository.findById(chatMessageDTO.getSenderId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid senderId"));
		Personnel receiver = personnelRepository.findById(chatMessageDTO.getReceiverId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid receiverId"));

		ChatRoom chatRoom = findOrCreateChatRoom(sender.getEmpId(), receiver.getEmpId());

		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setChatRoom(chatRoom);
		chatMessage.setSender(sender);
		chatMessage.setReceiver(receiver);
		chatMessage.setContent(chatMessageDTO.getContent());
		chatMessage.setType(chatMessageDTO.getType());
		chatMessage.setCreatedAt(LocalDateTime.now());
		chatMessage.setReadStatus(false);

		chatMessageRepository.save(chatMessage);
		log.info("채팅 메시지 저장 완료: {}", chatMessage);
	}

	// ChatRoom을 찾거나 생성하는 로직
	public ChatRoom findOrCreateChatRoom(String senderId, String receiverId) {
		String[] ids = { senderId, receiverId };
		Arrays.sort(ids);
		String roomId = String.join("_", ids);

		return chatRoomRepository.findByRoomId(roomId).orElseGet(() -> {
			ChatRoom newRoom = new ChatRoom();
			newRoom.setRoomId(roomId);
			newRoom.setName(senderId + " & " + receiverId);
			newRoom.setCreatedAt(LocalDateTime.now());
			return chatRoomRepository.save(newRoom);
		});
	}

	// 특정 두 사용자 간의 대화 기록을 가져오는 메서드 (수정)
	public List<ChatMessageDTO> getChatHistory(String senderId, String receiverId) {
		log.info("채팅 기록 조회 시작: {} <-> {}", senderId, receiverId);

		// 채팅방 ID를 생성
		String[] ids = { senderId, receiverId };
		Arrays.sort(ids);
		String roomId = String.join("_", ids);

		// 채팅방을 조회하거나 없으면 null 반환
		ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId).orElse(null);

		if (chatRoom == null) {
			return Collections.emptyList(); // 채팅방이 없으면 빈 목록 반환
		}

		// 해당 채팅방의 모든 메시지를 시간 순으로 조회
		List<ChatMessage> messages = chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom);

		// 엔티티를 DTO로 변환
		return messages.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	// 데이터베이스에서 읽지 않은 메시지를 가져오는 메서드
	public List<ChatMessageDTO> getUnreadMessages(String userId) {
		log.info("읽지 않은 메시지 불러오기: " + userId);

		Personnel receiver = personnelRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid userId"));

		List<ChatMessage> unreadMessages = chatMessageRepository.findByReceiverAndReadStatus(receiver, false);

		return unreadMessages.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	// 최근 메시지 목록을 가져오는 메서드 (수정)
	public List<ChatMessageDTO> getRecentMessages(String userId) {
		log.info("최근 메시지 조회 시작: {}", userId);
		// 이전에 보냈거나 받은 모든 메시지를 최신 순으로 조회
		List<ChatMessage> messages = chatMessageRepository
				.findBySender_EmpIdOrReceiver_EmpIdOrderByCreatedAtDesc(userId, userId);

		// 메시지 목록을 DTO로 변환
		return messages.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	// 엔티티를 DTO로 변환하는 헬퍼 메서드
	private ChatMessageDTO convertToDto(ChatMessage chatMessage) {
		ChatMessageDTO dto = new ChatMessageDTO();
		dto.setSenderId(chatMessage.getSender().getEmpId());
		dto.setSenderName(chatMessage.getSender().getName());
		dto.setContent(chatMessage.getContent());
		dto.setType(chatMessage.getType());
		dto.setCreatedAt(chatMessage.getCreatedAt());
		dto.setReadStatus(chatMessage.isReadStatus());
		return dto;
	}

	@Transactional // 트랜잭션 처리를 위해 어노테이션 추가
	public void markMessagesAsRead(String userId) {
		Personnel receiver = personnelRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid userId"));

		List<ChatMessage> unreadMessages = chatMessageRepository.findByReceiverAndReadStatus(receiver, false);

		// 읽지 않은 메시지들의 readStatus를 true로 변경
		unreadMessages.forEach(msg -> msg.setReadStatus(true));

		// 변경된 상태를 데이터베이스에 저장
		chatMessageRepository.saveAll(unreadMessages);

		log.info("사용자 {}의 메시지 {}개 읽음 상태로 업데이트 완료", userId, unreadMessages.size());
	}
}