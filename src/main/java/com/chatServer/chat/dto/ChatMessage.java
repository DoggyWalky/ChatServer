package com.chatServer.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

    private enum Type {
        QUIT, TALK
    }
    private Long roomId;
    private Long memberId;
    private String message;
}
