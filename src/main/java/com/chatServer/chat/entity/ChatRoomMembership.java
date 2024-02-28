package com.chatServer.chat.entity;

import com.chatServer.member.entity.Member;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name="room_membership")
public class ChatRoomMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="membership_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false,name="member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false,name="room_id")
    private ChatRoom chatRoom;

    @Column(name="visible")
    private Boolean isVisible;

    @Column(name="left_at")
    private LocalDateTime leftAt;
}
