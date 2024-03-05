package com.chatServer.chat.dto.response;

import com.chatServer.chat.entity.Chat;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageResponse {

    private Long chatId;

    private Long memberId;

    private String createdAt;

    private String content;

    private Boolean readYn;

    private Boolean deleteYn;

    public ChatMessageResponse(Long chatId,Long memberId, LocalDateTime createdAt, String content, Boolean readYn, Boolean deleteYn) {
        this.chatId = chatId;
        this.memberId = memberId;
        this.createdAt = createdAt.toString().replace("T"," ");
        this.content = content;
        this.readYn = readYn;
        this.deleteYn = deleteYn;
    }

    @JsonCreator
    public ChatMessageResponse(@JsonProperty("chatId") Long chatId,
                            @JsonProperty("memberId") Long memberId,
                               @JsonProperty("createdAt") String createdAt,
                               @JsonProperty("content") String content,
                               @JsonProperty("readYn") Boolean readYn,
                               @JsonProperty("deleteYn") Boolean deleteYn
                               ) {
        this.chatId = chatId;
        this.memberId = memberId;
        this.createdAt = createdAt;
        this.content = content;
        this.readYn = readYn;
        this.deleteYn = deleteYn;
    }

    public ChatMessageResponse(Chat chat) {
        this.chatId = chat.getId();
        this.memberId = chat.getMember().getId();
        this.createdAt = chat.getCreatedAt().toString().replace("T", " ").substring(0,19);
        this.content = chat.getContent();
        this.readYn = chat.getReadYn();
        this.deleteYn = chat.getDeleteYn();
    }


}
