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

    // 새로운 메시지인지 여부
    private Boolean isNewMessage;

    // JobPostId
    private Long jobPostId;

    // 상대방 나간 유무 체크
    private Boolean isLeft;

    public ChatRoomResponse(Long roomId, Long opponentId, String opponentNickname, String image, String lastMessage, Boolean readYn,Long senderId, LocalDateTime leftAt, Long jobPostId) {
        this.roomId = roomId;
        this.opponentId = opponentId;
        this.opponentNickname = opponentNickname;
        this.image = image;
        this.lastMessage = lastMessage;
        this.isNewMessage = opponentId == senderId ? !readYn : false;
        this.isLeft = leftAt==null ? false : true;
        this.jobPostId = jobPostId;
    }
}
