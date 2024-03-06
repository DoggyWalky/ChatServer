package com.chatServer.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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

    // JobPost Id
    private Long jobPostId;

    // 상대방 나간 유무 체크
    private Boolean isLeft;

    public ChatRoomResponse(Long roomId, Long opponentId, String opponentNickname, String image, String lastMessage, Boolean readYn, LocalDateTime leftAt, Long jobPostId) {
        this.roomId = roomId;
        this.opponentId = opponentId;
        this.opponentNickname = opponentNickname;
        this.image = image;
        this.lastMessage = lastMessage;
        this.readYn = readYn;
        this.isLeft = leftAt==null ? false : true;
        this.jobPostId = jobPostId;
    }
}
