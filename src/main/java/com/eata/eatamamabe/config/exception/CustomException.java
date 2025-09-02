package com.eata.eatamamabe.config.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;

    public CustomException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public static CustomException notFound(String message) {
        return new CustomException("COMMON.NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
    public static CustomException badRequest(String message) {
        return new CustomException("COMMON.BAD_REQUEST", message, HttpStatus.BAD_REQUEST);
    }
    public static CustomException unauthorized(String message) {
        return new CustomException("AUTH.UNAUTHORIZED", message, HttpStatus.UNAUTHORIZED);
    }
    public static CustomException forbidden(String message) {
        return new CustomException("AUTH.FORBIDDEN", message, HttpStatus.FORBIDDEN);
    }
    public static CustomException conflict(String message) {
        return new CustomException("COMMON.CONFLICT", message, HttpStatus.CONFLICT);
    }
}
