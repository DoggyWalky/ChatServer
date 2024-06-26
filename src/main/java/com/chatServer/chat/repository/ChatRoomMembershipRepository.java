package com.chatServer.chat.repository;

import com.chatServer.chat.dto.response.ChatRoomResponse;
import com.chatServer.chat.entity.ChatRoomMembership;
import com.chatServer.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMembershipRepository extends JpaRepository<ChatRoomMembership, Long> {

    @Query("select new com.chatServer.chat.dto.response.ChatRoomResponse(crms.chatRoom.id,opponent.member.id, opponent.nickName, opponent.profileImage, c.content,c.deleteYn, c.readYn,c.member.id, crms2.leftAt, crms.chatRoom.jobPostId) from ChatRoomMembership crms " +
            "join ChatRoomMembership crms2 on crms2.chatRoom.id=crms.chatRoom.id and crms2.opponent.id = :memberId " +
            "join MemberProfileInfo opponent " +
            "on crms.opponent.id = opponent.member.id and opponent.deletedYn = false " +
            "join Chat c on c.id=crms.chatRoom.lastChatId " +
            "where crms.member.id = :memberId and crms.isVisible = true and crms.leftAt is null and crms.chatRoom.deleteAt is null "+
            "order by c.createdAt desc"
    )
    List<ChatRoomResponse> findChatRoomList(@Param("memberId") Long memberId);


    @Query("select rm.opponent from ChatRoomMembership rm " +
            "where rm.chatRoom.id = :roomId " +
            "and rm.member.id = :memberId " +
            "and rm.leftAt is null " +
            "and rm.chatRoom.deleteAt is null")
    Optional<Member> findOpponentId(@Param("roomId") Long roomId, @Param("memberId") Long memberId);

    @Query("select rm from ChatRoomMembership rm " +
            "where rm.chatRoom.id = :roomId " +
            "and rm.member.id = :memberId " +
            "and rm.opponent.id = :opponentId " +
            "and rm.leftAt is null")
    Optional<ChatRoomMembership> findValidChatRoom(@Param("roomId") Long roomId, @Param("memberId") Long memberId, @Param("opponentId") Long opponentId);

    @Query("select rm from ChatRoomMembership rm " +
            "where rm.chatRoom.id = :roomId " +
            "and rm.member.id = :memberId " +
            "and rm.opponent.id = :opponentId ")
    Optional<ChatRoomMembership> findChatRoom(@Param("roomId") Long roomId, @Param("memberId") Long memberId, @Param("opponentId") Long opponentId);

    @Modifying
    @Query("delete from ChatRoomMembership rm where rm.chatRoom.id IN :rooms")
    void deleteMemberShipByScheduling(@Param("rooms") List<Long> rooms);
}
