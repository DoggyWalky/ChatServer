package com.chatServer.subscriber;

import com.chatServer.chat.dto.ChatMessage;
import com.chatServer.chat.dto.RedisChatMessage;
import com.chatServer.chat.dto.response.ChatMessageResponse;
import com.chatServer.chat.entity.Chat;
import com.chatServer.chat.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatMessageSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    private final ChatService chatService;

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 RedisSubscriber가 해당 메시지를 받아 처리함
     */
    public void sendMessage(String publishMessage) {
        try {
            //RedisChatMessage 객체로 매핑
            RedisChatMessage redisChatMessage = objectMapper.readValue(publishMessage, RedisChatMessage.class);

            // 채팅방을 구독한 클라이언트에게 메시지 발송(Redis의 토픽에 메시지 발행 후 작업)
            messagingTemplate.convertAndSend("/sub/chat/room/"+redisChatMessage.getRoomId(), redisChatMessage.getChatMessageResponse());

            // 채팅 상대 및 본인의 채팅방 목록에게 renew 전달하기
            messagingTemplate.convertAndSend("/sub/chatRoom/renew/"+redisChatMessage.getReceiverId(),redisChatMessage);
            messagingTemplate.convertAndSend("/sub/chatRoom/renew/"+redisChatMessage.getChatMessageResponse().getMemberId(),redisChatMessage);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
