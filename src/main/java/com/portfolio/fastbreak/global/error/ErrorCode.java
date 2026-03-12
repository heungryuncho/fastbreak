package com.portfolio.fastbreak.global.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "올바르지 않은 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 오류가 발생했습니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "존재하지 않는 회원입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "M002", "이미 존재하는 이메일입니다."),

    // Game
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "존재하지 않는 경기입니다."),

    // Seat
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "존재하지 않는 좌석입니다."),
    SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "S002", "이미 예매된 좌석입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }

}
