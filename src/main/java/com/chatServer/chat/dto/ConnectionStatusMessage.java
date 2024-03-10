package com.chatServer.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ConnectionStatusMessage {

    public enum Type {
        CONNECT, DISCONNECT
    }

    private Long memberId;
    private Long opponentId;
    private Long chatRoomId;
    private ConnectionStatusMessage.Type type;
}
