package com.chatServer.handler;

import com.chatServer.constant.ConstantPool;
import com.chatServer.exception.ErrorCode;
import com.chatServer.exception.ErrorResponse;
import com.chatServer.security.jwt.HmacAndBase64;
import com.chatServer.security.jwt.RefreshTokenProvider;
import com.chatServer.security.jwt.TokenProvider;
import com.chatServer.security.redis.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final TokenProvider tokenProvider;

    private final RefreshTokenProvider refreshTokenProvider;

    private final RedisService redisService;

    private final HmacAndBase64 hmacAndBase64;

    private final ObjectMapper objectMapper;

    private SimpMessagingTemplate messagingTemplate;

    private final ApplicationContext applicationContext;

    private SimpMessagingTemplate getMessagingTemplate() {
        if (messagingTemplate == null) {
            messagingTemplate = applicationContext.getBean(SimpMessagingTemplate.class);
        }
        return messagingTemplate;
    }



    // Websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    // preSend 메서드는 메시지가 채널을 통해 실제로 전송되기 전에 호출된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        boolean isAuthenticated = validAuthenticate(accessor);
        if (StompCommand.DISCONNECT != accessor.getCommand() && !isAuthenticated) {
            String renewToken = null;
            try {
                renewToken = returnValidRefreshAuthenticate(accessor);
            } catch (Exception e) {
                log.info("StompHandler Crypt 에러 발생 {} :", e);
            } finally {
                ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.UNAUTHORIZED.name());
                if (renewToken != null) {
                    try {
                        String jsonErrorResponse = objectMapper.writeValueAsString(errorResponse);
                        String destination = "/queue/authorization-error";

                        // 헤더 설정
                        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
                        headerAccessor.setSessionId(accessor.getSessionId());
                        headerAccessor.setLeaveMutable(true);
                        headerAccessor.setHeader(ConstantPool.AUTHORIZATION_HEADER, renewToken);

                        messagingTemplate.convertAndSendToUser(accessor.getSessionId(),destination,jsonErrorResponse,headerAccessor.getMessageHeaders());
                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                } else {
                    try {
                        String jsonErrorResponse = objectMapper.writeValueAsString(errorResponse);
                        String sessionId = accessor.getSessionId();
                        String destination = "/queue/authorization-error";

                        messagingTemplate.convertAndSendToUser(sessionId, destination, jsonErrorResponse);
                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }

        } else if (StompCommand.CONNECT == accessor.getCommand()) {
            log.info("CONNECT {}");
        } else if (StompCommand.SEND == accessor.getCommand()) {
            log.info("StompHandler Send 도착");
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            log.info("SUBSCRIBE");
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            log.info("DISCONNECT");
        }


        return message;
    }

    public String resolveToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
            return token.substring(7);
        }
        return null;
    }

    public boolean validAuthenticate(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader(ConstantPool.AUTHORIZATION_HEADER);
        System.out.println("token :" +token);
        String jwt = resolveToken(token);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            log.info("StompHandler JWT 토큰 인증 성공");
            return true;
        }
        log.info("StompHandler JWT 토큰 인증 실패");
        return false;
    }

    public String returnValidRefreshAuthenticate(StompHeaderAccessor accessor) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String token = accessor.getFirstNativeHeader(ConstantPool.REFRESH_HEADER);
        String refreshToken = resolveToken(token);
        String clientIp = (String) accessor.getSessionAttributes().get("clientIp");

        log.info("StompHandler clientIp {} :", clientIp);


        if (StringUtils.hasText(refreshToken) && refreshTokenProvider.validateToken(refreshToken,clientIp)) {
            Authentication authentication = tokenProvider.getAuthentication(refreshToken);

            if (redisService.getRefreshToken("refresh:"+
                    hmacAndBase64.crypt(clientIp,"HmacSHA512")+"_"+authentication.getName()).equals(refreshToken)) {

                String renewToken = tokenProvider.createToken(authentication);
                return renewToken;
            }

        }
        return null;

    }

}
