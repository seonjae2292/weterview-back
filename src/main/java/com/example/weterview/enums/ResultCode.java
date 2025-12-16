package com.example.weterview.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResultCode {

    // ============================
    // 1. [정상 처리] : Success (S)
    // ============================
    SUCCESS(HttpStatus.OK, "S001", "성공적으로 처리되었습니다."),

    // 데이터 생성 완료 (201 Created) - 필요하다면 분리
    CREATED(HttpStatus.CREATED, "S002", "리소스가 생성되었습니다."),

    // ============================
    // 2. [비즈니스 로직 분기] : Business (B)
    // ============================
    // 프론트엔드가 '성공'으로 간주하되, 팝업을 띄우거나 다른 화면으로 유도해야 하는 경우
    REGISTERED_MEMBER(HttpStatus.OK, "B001", "이미 가입된 회원입니다. 로그인 페이지로 이동합니다."),
    LOGIN_REQUIRED(HttpStatus.OK, "B002", "로그인이 필요한 서비스입니다."),

    // ============================
    // 3. [에러 상황] : Error (E) 또는 도메인별 분리
    // ============================

    // --- Global (G) ---
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G002", "입력값이 올바르지 않습니다."),
    MISSING_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G003", "필수 입력 값이 누락되었습니다."),

    // --- Auth (A) ---
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증되지 않은 사용자입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다."), // 세분화 예시

    // --- User (U) ---
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "존재하지 않는 사용자입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "U002", "이미 존재하는 닉네임입니다."),

    // --- StudyGroup (ST) ---
    STUDY_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "ST001", "존재하지 않는 스터디 그룹입니다."),
    STUDY_GROUP_FULL(HttpStatus.CONFLICT, "ST002", "스터디 그룹 정원이 가득 찼습니다."),

    // 로직상 "이미 삭제됨"과 "찾을 수 없음"을 프론트가 다르게 처리해야 한다면 코드를 분리
    ALREADY_DELETED_STUDY_GROUP(HttpStatus.BAD_REQUEST, "ST003", "이미 삭제된 스터디 그룹입니다."),

    // 날짜 관련 오류를 뭉뚱그릴지, 나눌지 결정 (여기선 나눔)
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "ST004", "시작 날짜는 종료 날짜보다 앞서야 합니다."),
    PAST_DATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "ST005", "과거 날짜는 선택할 수 없습니다."),
    INVALID_RECRUITMENT_NUMBER(HttpStatus.BAD_REQUEST, "ST006", "모집 인원 설정이 올바르지 않습니다."),

    // --- StudyGroupMember (STM) ---
    // 409 Conflict: 리소스의 현재 상태와 충돌 (이미 가입됨, 이미 거절됨 등)
    ALREADY_APPLIED_MEMBER(HttpStatus.CONFLICT, "STM001", "이미 신청한 스터디 그룹입니다."),
    ALREADY_ACCEPTED_MEMBER(HttpStatus.CONFLICT, "STM002", "이미 참여 중인 스터디 그룹입니다."),
    ALREADY_REFUSED_MEMBER(HttpStatus.CONFLICT, "STM003", "가입이 거절된 그룹입니다."),
    STUDY_GROUP_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "STM004", "해당 스터디 그룹 신청 이력이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}