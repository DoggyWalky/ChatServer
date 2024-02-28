package com.chatServer.chat.service;

import com.chatServer.chat.dto.ChatRoomMessage;
import com.chatServer.chat.entity.ChatRoom;
import com.chatServer.chat.entity.ChatRoomMembership;
import com.chatServer.chat.repository.ChatRepository;
import com.chatServer.chat.repository.ChatRoomMembershipRepository;
import com.chatServer.chat.repository.ChatRoomRepository;
import com.chatServer.exception.ApplicationException;
import com.chatServer.member.entity.Member;
import com.chatServer.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final ChatRepository chatRepository;

    private final ChatRoomMembershipRepository chatRoomMembershipRepository;

    public void createChatRoom(ChatRoomMessage message) {
        // Todo: 해당 채팅방이 존재하는지 DB에서 확인
        // Todo: 만약 채팅 서버에서 에러가 발생했을 때 어떻게 클라이언트에게 전달할지 고민해야한다
        if (chatRoomRepository.findChatRoom(message.getJobPostId(), message.getSenderId()).isEmpty()) {
            // 채팅방 생성
            ChatRoom room = new ChatRoom(message.getJobPostId());
            chatRoomRepository.save(room);

            // 채팅방 멤버십 생성
            // Todo: 예외 시 어떻게 처리할지 작성
            Member sender = memberRepository.findById(message.getSenderId()).get();
            Member receiver = memberRepository.findById(message.getReceiverId()).get();
            ChatRoomMembership senderRoomMembership = new ChatRoomMembership(sender, room);
            ChatRoomMembership receiverRoomMembership = new ChatRoomMembership(receiver, room);
            chatRoomMembershipRepository.save(senderRoomMembership);
            chatRoomMembershipRepository.save(receiverRoomMembership);
        } else  {
            System.out.println("채팅방 존재");
        }
    }

}
