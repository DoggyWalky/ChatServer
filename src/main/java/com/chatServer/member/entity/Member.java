package com.chatServer.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, name="member_id")
    private Long id;
    @Column(updatable = false)
    private String email;
    @Column(updatable = false)
    private String name;
    @Column(updatable = false, name="created_at")
    private LocalDateTime createdAt;

    @Column(updatable = false)
    private boolean deletedYn;


}

