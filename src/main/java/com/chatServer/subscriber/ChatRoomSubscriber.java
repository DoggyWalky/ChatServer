package com.chatServer.subscriber;

import com.chatServer.chat.dto.ChatMessage;
import com.chatServer.chat.dto.ChatRoomMessage;
import com.chatServer.chat.dto.response.ChatMessageResponse;
import com.chatServer.chat.dto.response.ChatStatusResponse;
import com.chatServer.chat.service.ChatService;
import com.chatServer.constant.ResponseCode;
import com.chatServer.exception.ApplicationException;
import com.chatServer.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatRoomSubscriber {

    private final ObjectMapper objectMapper;

    private final ChatService chatService;

    private final SimpMessageSendingOperations messagingTemplate;

    private final RedissonClient redissonClient;



    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 RedisSubscriber가 해당 메시지를 받아 처리함
     */
    public void sendMessage(String publishMessage) {
        ChatRoomMessage chatRoomMessage = null;
        String lockKey = "lock:chatRoom:" + publishMessage.hashCode();
        RLock lock = redissonClient.getLock(lockKey);


        try {
            //ChatMessage 객체로 매핑
            chatRoomMessage = objectMapper.readValue(publishMessage, ChatRoomMessage.class);
            System.out.println("chatRoom Message 도착");
            System.out.println(chatRoomMessage.toString());

            if (lock.tryLock(100,5000, TimeUnit.MILLISECONDS)) {
                try {
                    // 타입(QUIT, UNVISIBLE, CREATE)에 따른 처리
                    if (chatRoomMessage.getType() == ChatRoomMessage.Type.CREATE) {

                        // 채팅방 생성 로직(채팅 생성 로직 포함)
                        ChatMessage message = chatService.createChatRoom(chatRoomMessage);

                        // 채팅방을 구독한 클라이언트에게 메시지 발송(Redis의 토픽에 메시지 발행 후 작업)
                        // 채팅 전송 로직
                        messagingTemplate.convertAndSend("/sub/chat-room/renew/"+chatRoomMessage.getReceiverId(),message);
                        messagingTemplate.convertAndSend("/sub/chat-room/renew/"+chatRoomMessage.getSenderId(),message);
                    } else if (chatRoomMessage.getType() == ChatRoomMessage.Type.UNVISIBLE) {
                        // 채팅방 안보이게 설정
                        chatService.unvisibleChatRoom(chatRoomMessage);

                        // 채팅방을 구독한 클라이언트에게 메시지 발송
                        messagingTemplate.convertAndSend("/sub/chat-room/renew/"+chatRoomMessage.getSenderId(),new ChatStatusResponse(ResponseCode.UNVISIBLE_COMPLETED));
                    } else if (chatRoomMessage.getType() == ChatRoomMessage.Type.QUIT) {
                        // 채팅방 나가도록 설정
                        ChatMessageResponse chatMessageResponse = chatService.quitChatRoom(chatRoomMessage);

                        if (chatMessageResponse != null) {
                            // 채팅방 자체에 나가기 메시지 전송
                            messagingTemplate.convertAndSend("/sub/chat/room/"+chatRoomMessage.getChatRoomId(),chatMessageResponse);
                        }

                        // 채팅방을 구독한 클라이언트에게 메시지 발송
                        messagingTemplate.convertAndSend("/sub/chat-room/renew/"+chatRoomMessage.getSenderId(),new ChatStatusResponse(ResponseCode.QUIT_COMPLETED));
                        messagingTemplate.convertAndSend("/sub/chat-room/renew/"+chatRoomMessage.getReceiverId(),new ChatStatusResponse(ResponseCode.QUIT_COMPLETED));
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                log.info("Could not acquire lock for key: " + lockKey);
            }




        } catch (ApplicationException e) {
            // TODO: 예외 발생 시 해당 구독자(클라이언트)에게 예외 메시지 보내기 구현
            log.error("Exception {}", e);
            messagingTemplate.convertAndSend("/sub/error-message/" +chatRoomMessage.getSenderId(), new ChatStatusResponse(e.getErrorCode()));
        } catch (Exception e) {
            log.error("Exception {}", e);
            messagingTemplate.convertAndSend("/sub/error-message/" +chatRoomMessage.getSenderId(), new ChatStatusResponse(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }
}
