package com.chatServer.chat.controller;

import com.chatServer.chat.dto.ChatMessage;
import com.chatServer.chat.dto.ConnectionStatusMessage;
import com.chatServer.chat.dto.response.ChatMessageResponse;
import com.chatServer.chat.dto.response.ChatRoomResponse;
import com.chatServer.chat.dto.response.ChatStatusResponse;
import com.chatServer.chat.service.ChatService;
import com.chatServer.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {

    private final ChatService chatService;

    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        log.info("메시지 도착 : {}", message.toString());
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        try {
            chatService.sendChatMessage(message);
        } catch (ApplicationException e) {
            messagingTemplate.convertAndSend("/sub/errorMessage/"+message.getMemberId(), new ChatStatusResponse(e.getErrorCode()));
        }
    }

    /**
     * websocket "/pub/chat/modifyConnectionStatus"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/modifyConnectionStatus")
    public void modifyConnectionStatus(ConnectionStatusMessage message) {
        log.info("메시지 도착 : {}", message.toString());
        chatService.modifyConnectionStatus(message);
    }

    @GetMapping("/chat/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getRoomList(Principal principal) {
        Long memberId = Long.parseLong(principal.getName());

        List<ChatRoomResponse> roomList = chatService.getRoomList(memberId);
        return new ResponseEntity<>(roomList,HttpStatus.OK);
    }

    // TODO: 채팅 읽음 로직 추가해야한다 또한 채팅방에 있을 시 클라이언트 화면 상에서 채팅 읽음 로직 어떻게 구성할지 생각
    // TODO: 상대가 채팅방을 보고 있을 시 채팅 치면 채팅 읽음으로 수정이 되어야함
    @GetMapping("/chat/{roomId}")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(@PathVariable("roomId") Long roomId,Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        List<ChatMessageResponse> chatMessages = chatService.getChatMessages(roomId, memberId);
        return new ResponseEntity<>(chatMessages, HttpStatus.OK);
    }

}
