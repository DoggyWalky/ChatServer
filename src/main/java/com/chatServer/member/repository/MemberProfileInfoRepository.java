package com.chatServer.member.repository;

import com.chatServer.member.entity.MemberProfileInfo;

import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberProfileInfoRepository extends JpaRepository<MemberProfileInfo, Long> {

}
