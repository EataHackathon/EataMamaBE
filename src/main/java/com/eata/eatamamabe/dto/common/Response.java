package com.eata.eatamamabe.dto.common;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Response<T> {

    private T data;
    private String status;
    private String serverDateTime;
    private String errorCode;
    private String errorMessage;

    public Response(T data) {
        this.data = data;
        this.status = "SUCCESS";
        this.serverDateTime = LocalDateTime.now().toString();
    }

    public Response(String status, String errorCode, String errorMessage) {
        this.data = null;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.serverDateTime = LocalDateTime.now().toString();
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(data);
    }

    public static <T> Response<T> fail(String errorCode, String errorMessage) {
        return new Response<>("FAIL", errorCode, errorMessage);
    }

    public static <T> Response<T> error(String errorMessage) {
        return new Response<>("ERROR", "SERVER_ERROR", errorMessage);
    }
}
