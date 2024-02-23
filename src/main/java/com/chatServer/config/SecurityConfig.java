package com.chatServer.config;

import com.chatServer.security.jwt.*;
import com.chatServer.security.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    private final RefreshTokenProvider refreshTokenProvider;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final HmacAndBase64 hmacAndBase64;

    private final RedisService redisService;

    private final CorsFilter corsFilter;



    public SecurityConfig(TokenProvider tokenProvider, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAccessDeniedHandler jwtAccessDeniedHandler, RedisService redisService, CorsFilter corsFilter,
                          HmacAndBase64 hmacAndBase64, RefreshTokenProvider refreshTokenProvider) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.redisService = redisService;
        this.corsFilter = corsFilter;
        this.hmacAndBase64 = hmacAndBase64;
        this.refreshTokenProvider = refreshTokenProvider;
    }




    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 로그인 API, 회원가입 API는 토큰이 없는 상태에서 요청이 들어오기 때문에 모두 permitAll 설정을 한다.
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers(HttpMethod.GET,"/**.css", "/**.js", "/**.png").permitAll()
                        .anyRequest().authenticated());

        http
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)

                );


        // 세션을 사용하지 않기 때문에 STATELESS로 설정
        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http
                // JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig 클래스도 적용해준다.
                .apply(new JwtSecurityConfig(tokenProvider, refreshTokenProvider, redisService, hmacAndBase64));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> {
            web.ignoring().requestMatchers(request -> request.getRequestURI().startsWith("/h2-console"));
            web.ignoring().requestMatchers(request -> request.getRequestURI().startsWith("/favicon.ico"));
        };
    }
}
