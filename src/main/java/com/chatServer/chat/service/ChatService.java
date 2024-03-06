package com.chatServer.chat.service;

import com.chatServer.chat.dto.ChatMessage;
import com.chatServer.chat.dto.ChatRoomMessage;
import com.chatServer.chat.dto.RedisChatMessage;
import com.chatServer.chat.dto.response.ChatMessageResponse;
import com.chatServer.chat.dto.response.ChatRoomResponse;
import com.chatServer.chat.entity.Chat;
import com.chatServer.chat.entity.ChatRoom;
import com.chatServer.chat.entity.ChatRoomMembership;
import com.chatServer.chat.repository.ChatRepository;
import com.chatServer.chat.repository.ChatRoomMembershipRepository;
import com.chatServer.chat.repository.ChatRoomRepository;
import com.chatServer.exception.ApplicationException;
import com.chatServer.exception.ErrorCode;
import com.chatServer.member.entity.Member;
import com.chatServer.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ChatService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final ChatRepository chatRepository;

    private final ChatRoomMembershipRepository chatRoomMembershipRepository;

    private final RedisTemplate redisTemplate;

    private final ChannelTopic chatMessageTopic;


    /**
     * 메시지 생성 및 메시지 전달
     */
    public void sendChatMessage(ChatMessage message) {

        // Todo: 예외 발생 시 어떻게 클라이언트에게 전달해줄 지 결정해야한다.
        // Todo: TALK, QUIT 일 때 구분하기
        if (message.getType() == ChatMessage.Type.TALK) {
            // 메시지 저장
            Member member = memberRepository.findByMemberId(message.getMemberId()).orElseThrow(
                    () -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
            ChatRoom room = chatRoomRepository.findById(message.getRoomId()).orElseThrow(
                    () -> new ApplicationException(ErrorCode.ROOM_NOT_FOUND));
            Chat chat = Chat.createTalkMessage(member,room, message.getMessage());
            chatRepository.save(chat);

            // 상대 회원 pk 찾기
            Member opponentMember = chatRoomMembershipRepository.findOpponentId(room.getId(), message.getMemberId()).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

            // 채팅방 수정(마지막 메시지 UPDATE)
            room.modifyLastMessage(chat.getId());

            // 채팅방 멤버십에서 상대 유저의 visible을 true로 전환
            ChatRoomMembership membership = chatRoomMembershipRepository.findValidChatRoom(room.getId(), opponentMember.getId(), member.getId()).orElseThrow(() -> new ApplicationException(ErrorCode.ROOM_MEMBERSHIP_NOT_FOUND));
            membership.changeVisible(true);


            // 레디스 구독자들에게 메시지 publish
            redisTemplate.convertAndSend(chatMessageTopic.getTopic(), new RedisChatMessage(message.getRoomId(),opponentMember.getId(),new ChatMessageResponse(chat)));

        }

    }



    /**
     * 채팅 내역 불러오기
     */
    public List<ChatMessageResponse> getChatMessages(Long roomId) {
        // Todo: 예외 처리(웹소켓으로 전송할 필요 없이 ApplicationException 보내주기)
        List<ChatMessageResponse> chatList = chatRepository.findChatList(roomId);
        return chatList;
    }



    /**
     * 채팅방 목록 불러오기
     */
    public List<ChatRoomResponse> getRoomList(Long memberId) {
        return chatRoomMembershipRepository.findChatRoomList(memberId);
    }

    /**
     * 채팅방 생성 요청 시 채팅방/멤버십 생성
     */
    public ChatMessage createChatRoom(ChatRoomMessage roomMessage) {
        // Todo: 해당 채팅방이 존재하는지 DB에서 확인
        // Todo: 만약 채팅 서버에서 에러가 발생했을 때 어떻게 클라이언트에게 전달할지 고민해야한다
        if (chatRoomRepository.findChatRoom(roomMessage.getJobPostId(), roomMessage.getSenderId()).isEmpty()) {
            // 채팅방 생성
            ChatRoom room = new ChatRoom(roomMessage.getJobPostId());
            chatRoomRepository.save(room);

            // 채팅방 멤버십 생성
            // Todo: 예외 시 어떻게 처리할지 작성
            Member sender = memberRepository.findById(roomMessage.getSenderId()).get();
            Member receiver = memberRepository.findById(roomMessage.getReceiverId()).get();
            ChatRoomMembership senderRoomMembership = new ChatRoomMembership(sender,receiver, room);
            ChatRoomMembership receiverRoomMembership = new ChatRoomMembership(receiver,sender, room);
            chatRoomMembershipRepository.save(senderRoomMembership);
            chatRoomMembershipRepository.save(receiverRoomMembership);


            // 채팅 생성
            ChatMessage chatMessage = createEnterChatMessage(sender, room);


            return chatMessage;
        } else  {
            // Todo: 예외 처리
            System.out.println("채팅방 존재");
            throw new RuntimeException();
        }
    }


    /**
     * 채팅방 안보이도록 설정
     */
    public void unvisibleChatRoom(ChatRoomMessage roomMessage) throws ApplicationException {

        // 실제 유효한 채팅방이 있는지 검증
        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(roomMessage.getChatRoomId()).orElseThrow(() -> new ApplicationException(ErrorCode.ROOM_NOT_FOUND));

        // 해당 채팅방이 본인과 상대방이 속한 채팅방인지 검증 및 채팅방 멤버십 조회
        ChatRoomMembership memberShip = chatRoomMembershipRepository.findValidChatRoom(chatRoom.getId(), roomMessage.getSenderId(), roomMessage.getReceiverId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.ROOM_MEMBERSHIP_NOT_FOUND));

        // 채팅방 멤버십 수정
        memberShip.changeVisible(false);
    }

    /**
     * 채팅방 나가기 설정
     */
    public ChatMessageResponse quitChatRoom(ChatRoomMessage roomMessage) throws ApplicationException {
        // Todo: 만약 상대방도 나가기가 되어 있을 시 해당 채팅방은 delete해줘야한다
        // 실제 유효한 채팅방이 있는지 검증
        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(roomMessage.getChatRoomId()).orElseThrow(() -> new ApplicationException(ErrorCode.ROOM_NOT_FOUND));

        // 해당 채팅방이 본인과 상대방이 속한 채팅방인지 검증 및 채팅방 멤버십 조회
        ChatRoomMembership memberShip = chatRoomMembershipRepository.findValidChatRoom(chatRoom.getId(), roomMessage.getSenderId(), roomMessage.getReceiverId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.ROOM_MEMBERSHIP_NOT_FOUND));

        // 상대방이 나갔는지부터 체크
        ChatRoomMembership opponentShip = chatRoomMembershipRepository.findValidChatRoom(chatRoom.getId(), roomMessage.getReceiverId(), roomMessage.getSenderId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.ROOM_MEMBERSHIP_NOT_FOUND));

        // 상대방 나갔을 시 나도 나감 처리해주고 해당 채팅방 delete 하기
        if (opponentShip.getLeftAt() != null) {
            memberShip.getChatRoom().deleteChatRoom();
            // 내 채팅방 멤버십 나감 처리
            memberShip.quitChat();
            return null;
        } else {
            // 내 채팅방 멤버십 나감 처리
            memberShip.quitChat();
            // 마지막 채팅 내역 수정
            Chat chat = Chat.quitTalkMessage(memberShip.getMember(),memberShip.getChatRoom());
            chatRepository.save(chat);
            memberShip.getChatRoom().modifyLastMessage(chat.getId());
            ChatMessageResponse chatMessageResponse = new ChatMessageResponse(chat);
            return chatMessageResponse;
        }

    }


    /**
     * 문의 시(채팅방 생성) 입장 채팅 생성
     */
    public ChatMessage createEnterChatMessage(Member member, ChatRoom room) {
        Chat enterMessage = Chat.createEnterMessage(member, room);
        // 채팅 저장
        chatRepository.save(enterMessage);

        // 채팅방 마지막 메시지 설정
        room.modifyLastMessage(enterMessage.getId());
        return new ChatMessage(room.getId(), member.getId(),enterMessage.getContent(), ChatMessage.Type.ENTER);
    }

}
