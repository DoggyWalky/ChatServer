package com.chatServer.chat.dto.response;

import com.chatServer.constant.ResponseCode;
import com.chatServer.exception.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatStatusResponse {

    private Integer status;

    private String resultCode;

    private String message;


    public ChatStatusResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.resultCode = errorCode.name();
        this.message = errorCode.getMessage();
    }

    public ChatStatusResponse(ResponseCode responseCode) {
        this.status = responseCode.getStatus().value();
        this.resultCode = responseCode.name();
        this.message = responseCode.getMessage();
    }

}
