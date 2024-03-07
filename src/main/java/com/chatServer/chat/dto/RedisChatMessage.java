package com.chatServer.chat.dto;

import com.chatServer.chat.dto.response.ChatMessageResponse;
import lombok.Getter;

@Getter
public class RedisChatMessage {

    private Long roomId;

    private Long receiverId;
    private ChatMessageResponse chatMessageResponse;

    public RedisChatMessage(Long roomId,Long receiverId, ChatMessageResponse chatMessageResponse) {
        this.roomId = roomId;
        this.receiverId = receiverId;
        this.chatMessageResponse = chatMessageResponse;
    }

}
