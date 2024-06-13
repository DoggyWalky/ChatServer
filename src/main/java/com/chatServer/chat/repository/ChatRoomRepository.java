package com.chatServer.chat.repository;

import com.chatServer.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("select cr from ChatRoom cr join ChatRoomMembership crm on crm.member.id = :senderId where cr.jobPostId = :jobPostId and cr.deleteAt IS NULL")
    Optional<ChatRoom> findChatRoom(@Param("jobPostId") Long jobPostId, @Param("senderId") Long senderId);

    @Query("select cr from ChatRoom cr where cr.id = :roomId and cr.deleteAt is null")
    Optional<ChatRoom> findChatRoomById(@Param("roomId") Long roomId);

    @Query(value = "select cr.room_id from chatroom cr where cr.delete_at <= DATE_SUB(CONVERT_TZ(now(),'+00:00', '+09:00'), INTERVAL 6 MONTH)", nativeQuery = true)
    List<Long> findChatRoomToDelete();

    @Modifying
    @Query("delete from ChatRoom cr where cr.id IN :rooms")
    void deleteChatRoomsByScheduling(@Param("rooms") List<Long> rooms);
}
