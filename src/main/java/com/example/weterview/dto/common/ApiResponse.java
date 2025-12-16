package com.example.weterview.dto.common;

import com.example.weterview.enums.ResultCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // ⭐️ 핵심: null인 필드는 JSON에 포함되지 않음
public class ApiResponse<T> {

    private final T data;
    private final ErrorBody error;

    // 생성자를 private으로 막고, 정적 팩토리 메서드만 사용하도록 강제
    private ApiResponse(T data, ErrorBody error) {
        this.data = data;
        this.error = error;
    }

    // 1. 성공 응답 (data만 반환)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null);
    }

    // 성공이지만 반환할 데이터가 없는 경우 (예: 삭제 성공)
    public static ApiResponse<?> success() {
        return new ApiResponse<>(Collections.emptyMap(), null);
    }

    // 2. 실패 응답 (error만 반환)
    public static ApiResponse<?> fail(String code, String message) {
        return new ApiResponse<>(null, new ErrorBody(code, message));
    }

    public static ApiResponse<?> fail(ResultCode resultCode) {
        return new ApiResponse<>(null, new ErrorBody(resultCode.getCode(), resultCode.getMessage()));
    }

    public static ApiResponse<?> fail(ResultCode resultCode, String message) {
        return new ApiResponse<>(null, new ErrorBody(resultCode.getCode(), message));
    }

    // 내부 클래스: 에러 구조 정의
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ErrorBody {
        private final String code;
        private final String message;
    }
}