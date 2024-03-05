package com.chatServer.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomResponse {

    // 채팅방 pk
    private Long roomId;

    // 대화 상대 pk
    private Long opponentId;

    // 대화 상대 닉네임
    private String opponentNickname;

    // 대화 상대 이미지
    private String image;

    // 마지막 메시지
    private String lastMessage;

    // 읽음여부
    private Boolean readYn;

    // Todo: JobPost Id 추가

}
