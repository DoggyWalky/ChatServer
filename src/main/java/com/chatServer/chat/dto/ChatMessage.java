package com.chatServer.chat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class ChatMessage {

    public enum Type {
        QUIT, TALK, ENTER
    }
    private Long roomId;
    private Long memberId;
    private String message;

    private Type type;

    public ChatMessage(Long roomId, Long memberId, String message, Type type) {
        this.roomId = roomId;
        this.memberId = memberId;
        this.message = message;
        this.type = type;
    }
}
