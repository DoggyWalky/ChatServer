package com.chatServer.chat.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name="chatroom")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="room_id")
    private Long id;

    @Column(name="jobpost_id")
    private Long jobPostId;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    public ChatRoom(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }


}
