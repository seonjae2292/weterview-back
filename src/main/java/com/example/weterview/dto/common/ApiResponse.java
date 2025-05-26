package com.example.weterview.dto.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    private ApiResponse(HttpStatus status, String message, T data) {
        this.status = status.value();
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(HttpStatus.OK, message, data);
    }

    public static <T> ApiResponse<T> NOT_FOUND(T data, String message) {
        return new ApiResponse<>(HttpStatus.NOT_FOUND, message, data);
    }

    public static <T> ApiResponse<T> BAD_REQUEST(T data, String message) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST, message, data);
    }
}
