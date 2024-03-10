package com.chatServer.chat.repository;

import com.chatServer.chat.dto.response.ChatMessageResponse;
import com.chatServer.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("select new com.chatServer.chat.dto.response.ChatMessageResponse(c.id,c.member.id,c.createdAt,c.content,c.readYn,c.deleteYn) " +
            "from Chat c where c.chatRoom.id = :roomId and c.chatRoom.deleteAt is null")
    List<ChatMessageResponse> findChatList(@Param("roomId") Long roomId);

    @Modifying
    @Query("delete from Chat c where c.chatRoom.id IN :rooms")
    void deleteChatByScheduling(@Param("rooms") List<Long> rooms);

    @Modifying
    @Query("update Chat c set c.readYn=true where c.chatRoom.id= :roomId and c.member.id <> :memberId")
    void updateChatRead(@Param("roomId") Long roomId, @Param("memberId") Long memberId);
}
