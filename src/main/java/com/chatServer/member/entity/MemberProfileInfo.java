package com.chatServer.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_profile_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    private String nickName;

    private String description;


    private boolean deletedYn;

    private String profileImage;

    @Column(updatable = false, name="created_at")
    private LocalDateTime createdDate;

    @Column(updatable = false, name="updated_at")
    private LocalDateTime updatedDate;

}
