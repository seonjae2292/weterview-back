package com.example.weterview.dto.common;

import com.example.weterview.enums.ResultCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final String code;
    private final String message;
    private final T data;

    // 생성자는 private으로 닫아서 외부에서 new 못하게 막음
    private ApiResponse(ResultCode resultCode, String message, T data) {
        this.code = resultCode.getCode();
        this.message = message;
        this.data = data;
    }

    private ApiResponse(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage(); // 메시지는 Enum에서 가져옴 (강제)
        this.data = data;
    }

    // ============================
    // 성공 응답 (200 OK)
    // ============================

    // 1. 데이터가 있는 성공
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResultCode.SUCCESS, data);
    }

    // 2. 데이터가 없는 성공
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ResultCode.SUCCESS, null);
    }

    // ============================
    // 비즈니스 응답 (200 OK 이지만 특정 상황)
    // ============================

    // 3. 특정 비즈니스 상황 (예: 회원가입 중복) - 메시지 커스텀 금지
    public static <T> ApiResponse<T> of(ResultCode resultCode) {
        return new ApiResponse<>(resultCode, null);
    }

    // 4. 특정 비즈니스 상황 + 데이터 포함 (예: 가입은 됐는데 추가 정보 필요해서 DTO 내려줌)
    public static <T> ApiResponse<T> of(ResultCode resultCode, T data) {
        return new ApiResponse<>(resultCode, data);
    }

    // ============================
    // 예외/실패 응답 (GlobalExceptionHandler에서 사용)
    // ============================

    // 5. 일반적인 에러 처리 (메시지 커스텀 금지, Enum 것만 사용)
    public static <T> ApiResponse<T> fail(ResultCode resultCode) {
        return new ApiResponse<>(resultCode, null);
    }

    // 6. ★예외★: 입력값 검증(Validation) 실패는 메시지가 동적이어야 함
    // (예: "이메일 형식이 아닙니다", "비밀번호는 8자 이상..." 등)
    public static <T> ApiResponse<T> validationFail(ResultCode resultCode, String customMessage) {
        return new ApiResponse<>(resultCode, customMessage, null);
    }
}