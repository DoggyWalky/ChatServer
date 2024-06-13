package com.chatServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static com.chatServer.constant.ConstantPool.FRONT_LOCAL_HOST;
import static com.chatServer.constant.ConstantPool.FRONT_SERVER_HOST;


@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
//        config.setAllowedOrigins(List.of("*",FRONT_SERVER_HOST, FRONT_LOCAL_HOST));
        config.setExposedHeaders(List.of("Authorization", "Refresh"));
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
