package com.chatServer.security.exception;

public class NoJwtTokenException extends RuntimeException {

    public NoJwtTokenException() {
        super();
    }

    public NoJwtTokenException(String message) {
        super(message);
    }

    public NoJwtTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoJwtTokenException(Throwable cause) {
        super(cause);
    }

    protected NoJwtTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

