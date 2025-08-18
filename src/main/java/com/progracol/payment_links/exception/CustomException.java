package com.progracol.payment_links.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final int status;
    private final String code;

    public CustomException(int status, String message, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }
}