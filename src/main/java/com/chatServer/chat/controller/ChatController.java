package com.chatServer.chat.controller;

import com.chatServer.chat.dto.ChatMessage;
import com.chatServer.chat.dto.response.ChatMessageResponse;
import com.chatServer.chat.dto.response.ChatRoomResponse;
import com.chatServer.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {

    private final ChatService chatService;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        log.info("메시지 도착 : {}", message.toString());
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        chatService.sendChatMessage(message);
    }

    @GetMapping("/chat/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getRoomList(Principal principal) {
        Long memberId = Long.parseLong(principal.getName());

        List<ChatRoomResponse> roomList = chatService.getRoomList(memberId);
        return new ResponseEntity<>(roomList,HttpStatus.OK);
    }

    @GetMapping("/chat/{roomId}")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(@PathVariable("roomId") Long roomId) {
        List<ChatMessageResponse> chatMessages = chatService.getChatMessages(roomId);
        return new ResponseEntity<>(chatMessages, HttpStatus.OK);

    }
}
