package com.chatServer.security.jwt;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class HmacAndBase64 {

    private final String secret;

    public HmacAndBase64(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }


    public String crypt(String ipAddress, String Algorithms)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

        //1. SecretKeySpec 클래스를 사용한 키 생성
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes("utf-8"), Algorithms);

        //2. 지정된  MAC 알고리즘을 구현하는 Mac 객체를 작성합니다.
        Mac hasher = Mac.getInstance(Algorithms);

        //3. 키를 사용해 이 Mac 객체를 초기화
        hasher.init(secretKey);

        //3. 암호화 하려는 데이터의 바이트의 배열을 처리해 MAC 조작을 종료
        byte[] hash = hasher.doFinal(ipAddress.getBytes());

        //4. Base 64 Encode to String
        return Base64.encodeBase64String(hash);
    }
}
