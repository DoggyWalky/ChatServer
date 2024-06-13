package com.chatServer.config;

import com.chatServer.security.jwt.HmacAndBase64;
import com.chatServer.security.jwt.JwtFilter;
import com.chatServer.security.jwt.RefreshTokenProvider;
import com.chatServer.security.jwt.TokenProvider;
import com.chatServer.security.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;

    private final RefreshTokenProvider refreshTokenProvider;

    private final RedisService redisService;

    private final HmacAndBase64 hmacAndBase64;

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(
                new JwtFilter(tokenProvider,refreshTokenProvider,redisService, hmacAndBase64),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}
