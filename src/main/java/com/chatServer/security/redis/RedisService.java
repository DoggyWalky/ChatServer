package com.chatServer.security.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService implements TokenStorageService {

    private final RedisTemplate<String,String> redisTemplate;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;


    @Override
    public String getRefreshToken(String key) {
        //opsForValue : Strings를 쉽게 Serialize / Deserialize 해주는 Interface
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    @Override
    public void setRefreshToken(String key, String value) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key,value, Duration.ofSeconds(refreshTokenValidityInSeconds));
    }

    @Override
    public void removeRefreshToken(String key) {
        redisTemplate.delete(key);
    }

    public String getChatUserRoomId(Long memberId) {
        Object chatUser = redisTemplate.opsForHash().get("chatUser", memberId.toString());
        if (chatUser!=null) {
            System.out.println("chatUser : ");
            System.out.println(chatUser.toString());
            return chatUser.toString();
        } else {
            return null;
        }
    }

    public void setChatUser(Long memberId, Long roomId) {
        redisTemplate.opsForHash().put("chatUser", memberId.toString(), roomId.toString());
    }

    public void removeChatUser(Long memberId) {
        redisTemplate.opsForHash().delete("chatUser", memberId.toString());
    }
}
