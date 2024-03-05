package com.chatServer.subscriber;

import com.chatServer.chat.dto.ChatMessage;
import com.chatServer.chat.dto.ChatRoomMessage;
import com.chatServer.chat.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatRoomSubscriber {

    private final ObjectMapper objectMapper;

    private final ChatService chatService;

    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 RedisSubscriber가 해당 메시지를 받아 처리함
     */
    public void sendMessage(String publishMessage) {
        try {
            //ChatMessage 객체로 매핑
            ChatRoomMessage chatRoomMessage = objectMapper.readValue(publishMessage, ChatRoomMessage.class);
            System.out.println("chatRoom Message 도착");
            System.out.println(chatRoomMessage.toString());

            // 채팅방 생성 로직(채팅 생성 로직 포함)
            ChatMessage message = chatService.createChatRoom(chatRoomMessage);

            // 채팅방을 구독한 클라이언트에게 메시지 발송(Redis의 토픽에 메시지 발행 후 작업)
            // 채팅 전송 로직
            messagingTemplate.convertAndSend("/sub/chatRoom/renew"+chatRoomMessage.getReceiverId(),message);
            messagingTemplate.convertAndSend("/sub/chatRoom/renew"+chatRoomMessage.getSenderId(),message);

        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
