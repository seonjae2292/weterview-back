package com.example.weterview.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Global
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL_500", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "GLOBAL_400", "입력값이 올바르지 않습니다."),

    // FORBIDDEN
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_403", "해당 리소스에 접근 권한이 없습니다"),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_401", "인증되지 않은 사용자입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404", "존재하지 않는 사용자입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "USER_400", "이미 존재하는 닉네임입니다."),

    // StudyGroup
    STUDY_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_404", "존재하지 않는 스터디 그룹입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}