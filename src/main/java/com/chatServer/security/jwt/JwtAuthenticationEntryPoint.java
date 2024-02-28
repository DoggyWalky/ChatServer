package com.chatServer.security.jwt;

import com.chatServer.exception.ErrorCode;
import com.chatServer.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(ErrorResponse.of(ErrorCode.UNAUTHORIZED.name()));

        response.setContentType("application/json");
        System.out.println("Authentication 불허");

        //유효한 토큰이 아닐 시 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(jsonInString);
    }
}