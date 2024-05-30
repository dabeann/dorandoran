package com.backend.dorandoran.common.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // global
    RESOURCE_NOT_FOUND(NOT_FOUND, "요청한 자원을 찾을 수 없습니다."),
    INVALID_REQUEST(BAD_REQUEST, "올바르지 않은 요청입니다."),
    INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "예상치 못한 내부 에러가 발생했습니다."),
    SECURITY_UNAUTHORIZED(UNAUTHORIZED, "인증에 실패하였습니다."),

    // auth
    WRONG_AUTH_CODE(BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    SMS_SEND_ERROR(INTERNAL_SERVER_ERROR, "문자 발송에 실패했습니다."),
    WRONG_PHONE_NUMBER(BAD_REQUEST, "올바르지 않은 핸드폰 번호입니다."),
    EXPIRED_AUTH_CODE(BAD_REQUEST, "만료된 인증번호입니다."),
    ALREADY_VERIFIED(BAD_REQUEST, "이미 인증된 핸드폰 번호입니다."),
    PHONE_NUMBER_NOT_VERIFIED(UNAUTHORIZED, "핸드폰 번호 인증이 완료되지 않았습니다."),
    ACCESS_DENIED(FORBIDDEN, "접근이 거부되었습니다."),

    // token
    INVALID_TOKEN(BAD_REQUEST, "올바르지 않은 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "만료된 토큰입니다."),
    EMPTY_TOKEN(BAD_REQUEST, "토큰이 존재하지 않습니다."),

    // user
    NOT_FOUND_USER(BAD_REQUEST, "존재하지 않는 회원입니다."),
    NOT_FOUND_USERNAME(BAD_REQUEST, "이름과 휴대폰번호가 일치하지 않아요."),

    // counsel
    NOT_FOUND_COUNSEL(BAD_REQUEST, "존재하지 않는 상담입니다."),
    ALREADY_CLOSED_COUNSEL(BAD_REQUEST, "이미 종료된 상담입니다."),

    // disease
    NOT_FOUND_DISEASE(BAD_REQUEST, "존재하지 않는 병명입니다."),

    // meditation duration
    NOT_FOUND_MEDITATION_DURATION(BAD_REQUEST, "존재하지 않는 명상 기간입니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;
}
