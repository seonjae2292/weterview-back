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
    MISSING_INPUT_VALUE(HttpStatus.BAD_REQUEST, "GLOBAL_400_1", "필수 입력 값이 누락되었습니다."),

    // FORBIDDEN
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_403", "해당 리소스에 접근 권한이 없습니다"),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_401", "인증되지 않은 사용자입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404", "존재하지 않는 사용자입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "USER_400", "이미 존재하는 닉네임입니다."),

    // StudyGroup
    STUDY_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_404", "존재하지 않는 스터디 그룹입니다."),
    ALREADY_DELETE(HttpStatus.BAD_REQUEST, "STUDY_400", "이미 삭제된 게시글 입니다."),

    // StudyGroupMember
    ALREADY_APPLIED_MEMBER(HttpStatus.CONFLICT, "STUDY_MEMBER_409", "이미 신청한 스터디 그룹원입니다."),
    ALREADY_ACCEPTED_MEMBER(HttpStatus.CONFLICT, "STUDY_MEMBER_409", "이미 수락된 스터디 그룹원입니다."),
    ALREADY_REFUSED_MEMBER(HttpStatus.CONFLICT, "STUDY_MEMBER_409_2", "이미 거절된 스터디 그룹원입니다. 재신청이 필요합니다."),
    STUDY_GROUP_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_MEMBER_404", "스터디 그룹에 신청한 이력이 없습니다"),;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}