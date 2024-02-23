package com.chatServer.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSecretInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, name="member_secret_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false,name="member_id")
    private Member member;

    @Column(updatable = false)
    private boolean deletedYn;

    @Column(updatable = false)
    private String phoneNumber;

    @Column(updatable = false, name="created_at")
    private LocalDateTime createdDate;

    @Column(updatable = false, name="updated_at")
    private LocalDateTime updatedDate;



}
