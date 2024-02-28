package com.chatServer.chat.entity;

import com.chatServer.member.entity.Member;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
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


}
