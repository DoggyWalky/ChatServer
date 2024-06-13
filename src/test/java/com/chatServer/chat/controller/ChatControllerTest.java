package com.chatServer.chat.controller;

import com.chatServer.chat.dto.response.ChatMessageResponse;
import com.chatServer.chat.dto.response.ChatRoomResponse;
import com.chatServer.chat.service.ChatService;
import com.chatServer.common.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureRestDocs
@Transactional
@DirtiesContext
class ChatControllerTest extends RestDocsTestSupport {

    @MockBean
    private ChatService chatService;

    @Autowired
    private UserDetailsService userDetailsService;



    private String email = "BLv8Uug7klqfirsmyHa/q1mzxA8nma90rdYVZdc60fY=";

    @DisplayName("채팅방 목록 조회 테스트")
    @Test
    public void getchatroomlist_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        ChatRoomResponse chatRoomResponse1 = new ChatRoomResponse(1L,1L,"상대방 닉네임","opponentImage.jpg","마지막 메시지입니다.",false,false,3L, LocalDateTime.now(),1L);
        ChatRoomResponse chatRoomResponse2 = new ChatRoomResponse(2L,3L,"상대방 닉네임","opponentImage.jpg","마지막 메시지입니다.",false,false,1L, LocalDateTime.now(),2L);
        ChatRoomResponse chatRoomResponse3 = new ChatRoomResponse(3L,1L,"상대방 닉네임","opponentImage.jpg","마지막 메시지입니다.",false,false,3L, LocalDateTime.now(),3L);

        List<ChatRoomResponse> chatRoomResponseList = Arrays.asList(chatRoomResponse1, chatRoomResponse2, chatRoomResponse3);

        Mockito.when(chatService.getRoomList(anyLong())).thenReturn(chatRoomResponseList);

        // when & then
        mockMvc.perform(get("/api/chat/rooms")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("[].roomId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("채팅방의 고유 번호"),
                                fieldWithPath("[].opponentId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("채팅 상대의 고유 번호"),
                                fieldWithPath("[].opponentNickname")
                                        .type(JsonFieldType.STRING)
                                        .description("채팅 상대의 닉네임"),
                                fieldWithPath("[].image")
                                        .type(JsonFieldType.STRING)
                                        .description("채팅 상대의 프로필 이미지 주소"),
                                fieldWithPath("[].lastMessage")
                                        .type(JsonFieldType.STRING)
                                        .description("채팅방의 마지막 메시지 내용"),
                                fieldWithPath("[].deleteYn")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("채팅방의 마지막 메시지 삭제 여부"),
                                fieldWithPath("[].isNewMessage")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("채팅방의 마지막 메시지가 새로운 메시지인지 여부"),
                                fieldWithPath("[].jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("해당 채팅방 관련 게시글의 고유 번호"),
                                fieldWithPath("[].isLeft")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("채팅 상대가 채팅방을 나간 여부")
                        )
                ));
    }

    @DisplayName("채팅 목록 조회 테스트")
    @Test
    public void getchatmessagelist_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        ChatMessageResponse chatMessageResponse1 = new ChatMessageResponse(1L,1L,LocalDateTime.now(),"첫번째 채팅",true,false);
        ChatMessageResponse chatMessageResponse2 = new ChatMessageResponse(2L,1L,LocalDateTime.now(),"두번째 채팅",true,false);
        ChatMessageResponse chatMessageResponse3 = new ChatMessageResponse(3L,1L,LocalDateTime.now(),"세번째 채팅",true,false);

        List<ChatMessageResponse> chatMessageResponseList = Arrays.asList(chatMessageResponse1, chatMessageResponse2, chatMessageResponse3);

        Mockito.when(chatService.getChatMessages(anyLong(),anyLong())).thenReturn(chatMessageResponseList);

        // when & then
        mockMvc.perform(get("/api/chat/{room-id}",1L)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("[].chatId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("채팅의 고유 번호"),
                                fieldWithPath("[].memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("채팅 작성자의 고유 번호"),
                                fieldWithPath("[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("채팅 생성 일시"),
                                fieldWithPath("[].content")
                                        .type(JsonFieldType.STRING)
                                        .description("채팅 내용"),
                                fieldWithPath("[].readYn")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("채팅 읽음 여부"),
                                fieldWithPath("[].deleteYn")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("채팅 삭제 여부")
                        )
                ));

    }

    @DisplayName("채팅 삭제 테스트")
    @Test
    public void deletechat_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        Mockito.doNothing().when(chatService).deleteChatMessage(anyLong(), anyLong(), anyLong());

        // when
        mockMvc.perform(delete("/api/chat/{chat-id}",1L)
                        .param("opponentId", "1")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.chatId").value(1L))
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("chatId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("삭제된 채팅의 고유 번호")
                        )
                ));
    }


}