package com.chatServer.chat.repository;

import com.chatServer.chat.dto.response.ChatMessageResponse;
import com.chatServer.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("select new com.chatServer.chat.dto.response.ChatMessageResponse(c.id,c.member.id,c.createdAt,c.content,c.readYn,c.deleteYn) " +
            "from Chat c where c.chatRoom.id = :roomId and c.chatRoom.deleteYn = false")
    List<ChatMessageResponse> findChatList(@Param("roomId") Long roomId);
}
