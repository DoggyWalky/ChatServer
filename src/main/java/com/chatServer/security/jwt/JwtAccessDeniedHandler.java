package com.chatServer.security.jwt;

import com.chatServer.exception.ErrorCode;
import com.chatServer.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(ErrorResponse.of(ErrorCode.ACCESS_FORBIDDEN.name()));

        response.setContentType("application/json");
        //필요한 권한이 없이 접근하려 할때 403
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(jsonInString);
    }
}
