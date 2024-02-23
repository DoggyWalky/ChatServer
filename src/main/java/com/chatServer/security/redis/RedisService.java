package com.chatServer.security.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
}
