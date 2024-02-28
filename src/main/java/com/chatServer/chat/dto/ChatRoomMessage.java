package com.chatServer.chat.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChatRoomMessage {

    private enum Type {
        UNVISIBLE, QUIT, CREATE
    }

    private Long senderId;
    private Long receiverId;
    private Long ChatRoomId;
    private Type type;

}
