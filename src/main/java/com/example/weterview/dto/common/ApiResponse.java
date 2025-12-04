package com.example.weterview.dto.common;

import com.example.weterview.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

    // 성공이지만 반환할 데이터가 없는 경우 (예: 삭제 성공) -> 빈 객체 {} 라도 주는 게 관례상 좋음
    public static ApiResponse<?> success() {
        return new ApiResponse<>(null, null); // data가 null이면 빈 JSON {} 이 되거나, 설정을 통해 data: null로 줄 수 있음.
        // 보통은 success(null) 보다는 success("ok") 처럼 간단한 문자열이라도 주거나,
        // 프론트랑 협의해서 빈 data 필드를 줍니다. 여기서는 data: null이 아예 안나오게 됩니다.
    }

    // 2. 실패 응답 (error만 반환)
    public static ApiResponse<?> fail(String code, String message) {
        return new ApiResponse<>(null, new ErrorBody(code, message));
    }

    public static ApiResponse<?> fail(ErrorCode errorCode) {
        return new ApiResponse<>(null, new ErrorBody(errorCode.getCode(), errorCode.getMessage()));
    }

    public static ApiResponse<?> fail(ErrorCode errorCode, String message) {
        return new ApiResponse<>(null, new ErrorBody(errorCode.getCode(), message));
    }

    // 내부 클래스: 에러 구조 정의
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ErrorBody {
        private final String code;
        private final String message;
    }
}