package com.chatServer.chat.entity;

import com.chatServer.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false,name="member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false,name="room_id")
    private ChatRoom chatRoom;

    private String content;

    @Column(name="read_yn")
    private Boolean readYn;

    @Column(name="delete_yn")
    private Boolean deleteYn;

    @Column(updatable = false, name="created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public static Chat createEnterMessage(Member member,ChatRoom room) {
        return Chat.builder()
                .member(member)
                .chatRoom(room)
                .content("1:1 문의하기 입장하셨습니다.")
                .readYn(false)
                .deleteYn(false)
                .build();
    }

    public static Chat createTalkMessage(Member member, ChatRoom room,String content,boolean readYn) {
        return Chat.builder()
                .member(member)
                .chatRoom(room)
                .content(content)
                .readYn(readYn)
                .deleteYn(false)
                .build();
    }

    public static Chat quitTalkMessage(Member member, ChatRoom room, boolean readYn) {
        return Chat.builder()
                .member(member)
                .chatRoom(room)
                .content("채팅방을 나가셨습니다.")
                .readYn(readYn)
                .deleteYn(false)
                .build();
    }

    public void deleteChat() {
        this.deleteYn = true;
    }

}
