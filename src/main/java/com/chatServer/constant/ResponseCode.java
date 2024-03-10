package com.chatServer.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    // COMMON
    OK(HttpStatus.OK, "OK"),


    // CHAT
    UNVISIBLE_COMPLETED(HttpStatus.OK, "Unvisible is completed"),
    QUIT_COMPLETED(HttpStatus.OK, "Quit is completed");

    // MEMBER



    // TOKEN



    private HttpStatus status;
    private String message;
}
