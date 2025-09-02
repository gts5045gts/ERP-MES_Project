package com.bootstrap.study.groupware.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bootstrap.study.groupware.dto.ChatMessageDTO;
import com.bootstrap.study.groupware.entity.ChatMessage;
import com.bootstrap.study.groupware.entity.ChatRoom;
import com.bootstrap.study.groupware.repository.ChatMessageRepository;
import com.bootstrap.study.groupware.repository.ChatRoomRepository;
import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.repository.PersonnelRepository;

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
	        
	        // 1. DTO의 senderId와 receiverId를 사용해 Personnel 엔티티를 조회합니다.
	        Personnel sender = personnelRepository.findById(chatMessageDTO.getSenderId())
	                                             .orElseThrow(() -> new IllegalArgumentException("Invalid senderId"));
	        Personnel receiver = personnelRepository.findById(chatMessageDTO.getReceiverId())
	                                               .orElseThrow(() -> new IllegalArgumentException("Invalid receiverId"));
	        
	        // 2. ChatRoom을 조회하거나 새로 생성합니다.
	        ChatRoom chatRoom = findOrCreateChatRoom(sender.getEmpId(), receiver.getEmpId());
	        
	        // 3. DTO를 엔티티로 변환
	        ChatMessage chatMessage = new ChatMessage();
	        chatMessage.setChatRoom(chatRoom); // ⭐️ ChatRoom 엔티티 설정
	        chatMessage.setSender(sender);
	        chatMessage.setReceiver(receiver); // ⭐️ receiver 엔티티 설정
	        chatMessage.setContent(chatMessageDTO.getContent());
	        chatMessage.setType(chatMessageDTO.getType());
	        chatMessage.setCreatedAt(LocalDateTime.now());
	        chatMessage.setReadStatus(false);
	        
	        chatMessageRepository.save(chatMessage);
	        log.info("채팅 메시지 저장 완료: {}", chatMessage);
	    }
	    
	    // ⭐️ ChatRoom을 찾거나 생성하는 로직
	    private ChatRoom findOrCreateChatRoom(String senderId, String receiverId) {
	        // 두 ID를 정렬하여 고유한 roomId를 만듭니다.
	        String[] ids = {senderId, receiverId};
	        Arrays.sort(ids);
	        String roomId = String.join("_", ids);
	        
	        // 해당 roomId로 채팅방이 존재하는지 확인합니다.
	        return chatRoomRepository.findByRoomId(roomId)
	                                 .orElseGet(() -> {
	                                     // 없으면 새로운 채팅방을 생성합니다.
	                                     ChatRoom newRoom = new ChatRoom();
	                                     newRoom.setRoomId(roomId);
	                                     // 채팅방 이름 설정 (예시)
	                                     newRoom.setName(senderId + " & " + receiverId);
	                                     newRoom.setCreatedAt(LocalDateTime.now());
	                                     return chatRoomRepository.save(newRoom);
	                                 });
	    }

	// 데이터베이스에서 읽지 않은 메시지를 가져오는 메서드
	public List<ChatMessageDTO> getUnreadMessages(String userId) {
		log.info("읽지 않은 메시지 불러오기: " + userId);

		// userId를 사용해 Personnel 엔티티를 조회
		Personnel receiver = personnelRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid userId"));

		// 리포지토리의 쿼리 메서드를 사용해 읽지 않은 메시지를 조회
		List<ChatMessage> unreadMessages = chatMessageRepository.findByReceiverAndReadStatus(receiver, false);

		// 엔티티 목록을 DTO 목록으로 변환하여 반환
		return unreadMessages.stream().map(this::convertToDto) // convertToDto 메서드를 사용해 변환
				.collect(Collectors.toList());
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
}
