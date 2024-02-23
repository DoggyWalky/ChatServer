package com.chatServer.security.jwt;

import com.chatServer.exception.ApplicationException;
import com.chatServer.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Component
@Slf4j
public class RefreshTokenProvider implements InitializingBean {
    private static final String IP_ADDRESS_KEY = "ipAddress";

    private final String secret;

    private final long tokenValidityInMilliseconds;

    @Autowired
    private HmacAndBase64 hmacAndBase64;


    private Key key;


    public RefreshTokenProvider(@Value("${jwt.secret}") String secret,
                                @Value("${jwt.refresh-token-validity-in-seconds}") long tokenValidityInSeconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication, String ipAddress) {

        log.debug("Refresh token 생성 시 ipAddress : {}", ipAddress);
        String cryptIpAddress=null;


        try {
            cryptIpAddress = hmacAndBase64.crypt(ipAddress, "HmacSHA512");
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
            log.warn("암호화 과정에 에러 발생");
            throw new RuntimeException("암호화 과정에 에러 발생");
        }


        long now = System.currentTimeMillis();

        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(IP_ADDRESS_KEY, cryptIpAddress)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(new Date(now + this.tokenValidityInMilliseconds))
                .compact();

        log.debug("생성된 Refresh 토큰 : {}", token);

        return token;

    }

    public boolean validateToken(String token,String ipAddress) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.debug("Refresh claims : {}", claims.get(IP_ADDRESS_KEY));

            String cryptIpAddress = hmacAndBase64.crypt(ipAddress, "HmacSHA512");

            if (!claims.get(IP_ADDRESS_KEY).toString().equals(cryptIpAddress)) {
                log.debug("Refresh 토큰의 ipAddress와 현재 접속한 ipAddress가 일치하지 않음");
                return false;
            }

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 Refresh 토큰 서명입니다.");
            throw new ApplicationException(ErrorCode.INVALID_REFRESH_TOKEN);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 Refresh 토큰입니다.");
            throw new ApplicationException(ErrorCode.INVALID_REFRESH_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 Refresh 토큰입니다.");
            throw new ApplicationException(ErrorCode.INVALID_REFRESH_TOKEN);
        } catch (IllegalArgumentException e) {
            log.warn("Refresh 토큰이 잘못되었습니다.");
            throw new ApplicationException(ErrorCode.INVALID_REFRESH_TOKEN);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("예외발생");
        }

        return false;

    }
}
