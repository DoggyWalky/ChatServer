package com.chatServer.subscriber;

import com.chatServer.chat.dto.ChatRoomMessage;
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
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 RedisSubscriber가 해당 메시지를 받아 처리함
     */
    public void sendMessage(String publishMessage) {
        try {
            //ChatMessage 객체로 매핑
            System.out.println("publishMessage");
            System.out.println(publishMessage);
            ChatRoomMessage chatRoomMessage = objectMapper.readValue(publishMessage, ChatRoomMessage.class);
            System.out.println("chatRoom Message 도착");
            System.out.println(chatRoomMessage.toString());
            // 채팅방을 구독한 클라이언트에게 메시지 발송(Redis의 토픽에 메시지 발행 후 작업)
//            messagingTemplate.convertAndSend("/sub/chat/room/"+chatRoomMessage.getRoomId(), chatRoomMessage);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
