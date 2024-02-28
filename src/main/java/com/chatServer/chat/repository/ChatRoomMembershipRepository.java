package com.chatServer.chat.repository;

import com.chatServer.chat.entity.ChatRoomMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMembershipRepository extends JpaRepository<ChatRoomMembership, Long> {
}
