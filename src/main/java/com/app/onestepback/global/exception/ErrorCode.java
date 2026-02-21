package com.app.onestepback.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // [COMMON] 시스템 및 전역 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYS-001", "서버 내부 오류가 발생했습니다."),

    // [VALIDATION] 데이터 유효성 검증 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "VAL-001", "유효하지 않은 입력값입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "VAL-002", "잘못된 인자값입니다."),

    // [AUTH] 인증 및 권한 에러
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH-001", "로그인이 필요한 서비스입니다."),
    NOT_ARTIST(HttpStatus.FORBIDDEN, "AUTH-002", "아티스트 권한이 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH-003", "접근 권한이 없습니다."),
    OAUTH_TOKEN_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-004", "카카오 인증 서버로부터 토큰을 발급받는데 실패했습니다."),
    OAUTH_USER_INFO_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-005", "카카오 리소스 서버로부터 사용자 정보를 불러오는데 실패했습니다."),

    // [MEMBER] 회원 비즈니스 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM-001", "존재하지 않는 회원입니다."),
    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "MEM-002", "이미 존재하는 이메일입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "MEM-003", "아이디 또는 비밀번호가 일치하지 않습니다."),

    // [ARTIST] 아티스트 비즈니스 에러
    ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "ART-001", "존재하지 않는 아티스트입니다."),

    // [FEED] 피드 비즈니스 에러
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "FEED-001", "요청하신 피드가 존재하지 않습니다."),
    FEED_DELETE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "FEED-002", "삭제 권한이 없거나 이미 삭제된 피드입니다."),

    // [RELATIONSHIP] 팔로우/관계 관련 에러
    SELF_FOLLOW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "REL-001", "자기 자신을 팔로우할 수 없습니다."),

    // [SUBSCRIPTION] 구독 비즈니스 에러
    SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "SUS-001", "존재하지 않는 구독입니다."),
    SUBSCRIPTION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SUS-002", "이미 해당 아티스트를 구독 중입니다."),

    // [MEMBERSHIP] 멤버십 비즈니스 에러
    MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "MS-001", "존재하지 않는 멤버십입니다."),
    MEMBERSHIP_LEVEL_DUPLICATED(HttpStatus.BAD_REQUEST, "MS-002", "해당 등급의 멤버십이 이미 존재합니다."),
    MEMBERSHIP_PRICE_HIERARCHY_VIOLATION(HttpStatus.BAD_REQUEST, "MS-003", "상위 등급의 멤버십 가격은 하위 등급보다 비싸게 설정되어야 합니다."),
    MEMBERSHIP_HAS_ACTIVE_SUBSCRIBERS(HttpStatus.BAD_REQUEST, "MS-004", "활성 구독자가 존재하는 멤버십은 정보 변경이나 삭제가 불가능합니다."),
    MEMBERSHIP_INACTIVE(HttpStatus.BAD_REQUEST, "MS-005", "현재 가입할 수 없는 비활성 멤버십입니다."),

    // [PAYMENT] 결제 관련 에러
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PAY-001", "결제 승인에 실패했습니다."),
    PAYMENT_METHOD_NOT_FOUND(HttpStatus.NOT_FOUND, "PAY-002", "존재하지 않는 결제 수단입니다."),
    PAYMENT_SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAY-003", "결제는 승인되었으나 시스템 오류로 취소되었습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAY-004", "결제 내역을 찾을 수 없거나 접근 권한이 없습니다."),
    DEFAULT_PAYMENT_DELETE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "PAY-005", "기본 결제 수단은 삭제할 수 없습니다. 다른 결제 수단을 기본으로 설정한 후 삭제해주세요."),

    // [CROWD FUNDING]
    CROWD_FUNDING_NOT_FOUND(HttpStatus.BAD_REQUEST, "CFU-001", "크라우드 펀딩을 조회할 수 없습니다."),
    CROWD_FUNDING_NOT_PROCEEDING(HttpStatus.BAD_REQUEST, "CFU-002", "진행 중인 크라우드 펀딩만 처리할 수 있습니다."),
    CROWD_FUNDING_NOT_ENDED(HttpStatus.BAD_REQUEST, "CFU-003", "종료된 크라우드 펀딩만 상태를 변경할 수 있습니다."),
    CROWD_FUNDING_NOT_WAITING(HttpStatus.BAD_REQUEST, "CFU-004", "승인 대기 중인 펀딩만 승낙/거절 할 수 있습니다."),
    CROWD_FUNDING_DATES_REQUIRED(HttpStatus.BAD_REQUEST, "CFU-005", "아티스트가 펀딩을 오픈할 경우 시작일과 종료일은 필수입니다."),
    CROWD_FUNDING_INVALID_DATES(HttpStatus.BAD_REQUEST, "CFU-006", "종료일은 시작일보다 미래여야 합니다."),

    // [SETTLEMENT] 정산 비즈니스 에러
    SETTLEMENT_NO_AMOUNT(HttpStatus.BAD_REQUEST, "STL-001", "정산 가능한 금액이 존재하지 않습니다."),
    SETTLEMENT_PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "STL-002", "정산 처리할 유효한 결제 내역을 찾을 수 없습니다."),
    SETTLEMENT_BANK_INFO_REQUIRED(HttpStatus.BAD_REQUEST, "STL-003", "등록된 정산용 계좌 정보가 없습니다."),
    SETTLEMENT_ARTIST_INACTIVE(HttpStatus.FORBIDDEN, "STL-004", "비활성화된 회원 상태이므로 정산을 진행할 수 없습니다."),

    // [FILE]
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-001", "파일 업로드 처리 중 오류가 발생했습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "FILE-002", "지원하지 않는 파일 형식입니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE-003", "존재하지 않는 파일입니다."),
    FILE_SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-004", "파일 처리 중 일시적인 시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}