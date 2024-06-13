package com.chatServer.chat.scheduler;

import com.chatServer.chat.repository.ChatRepository;
import com.chatServer.chat.repository.ChatRoomMembershipRepository;
import com.chatServer.chat.repository.ChatRoomRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ChatCleanUpScheduler {

    private final ChatRoomRepository chatRoomRepository;

    private final ChatRepository chatRepository;

    private final ChatRoomMembershipRepository membershipRepository;

    /**
     * 소프트 삭제된 지 6개월이 지난 채팅방 및 관련 채팅 내역, 멤버십 정보들을 스케줄링한다
     * 매일 자정에 스케줄링이 동작하게 된다
     */
    // Todo: 채팅방, 채팅내역, 채팅방 멤버십 테이블 정리하는 로직 추가
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteChatRelatedData() {
        // 삭제해야할 채팅방 찾기
        // Mysql RDS 인스턴스 타임존 설정을 UTC로 해서 쿼리상으로 KST 시간으로 변환해줬다
        List<Long> roomsPkToDelete = chatRoomRepository.findChatRoomToDelete();

        if (!roomsPkToDelete.isEmpty()) {
            // 채팅방 삭제
            chatRoomRepository.deleteChatRoomsByScheduling(roomsPkToDelete);

            // 채팅 멤버십 정보 삭제
            membershipRepository.deleteMemberShipByScheduling(roomsPkToDelete);

            // 채팅 삭제
            chatRepository.deleteChatByScheduling(roomsPkToDelete);
        }

    }

}
