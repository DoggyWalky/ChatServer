package com.chatServer.chat.repository;

import com.chatServer.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("select cr from ChatRoom cr join ChatRoomMembership crm on crm.member.id = :senderId where cr.jobPostId = :jobPostId and cr.deleteYn = false")
    Optional<ChatRoom> findChatRoom(@Param("jobPostId") Long jobPostId, @Param("senderId") Long senderId);

    @Query("select cr from ChatRoom cr where cr.id = :roomId and cr.deleteYn = false")
    Optional<ChatRoom> findChatRoomById(@Param("roomId") Long roomId);
}
