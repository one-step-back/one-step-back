package com.app.onestepback.global.advice;

import com.app.onestepback.global.common.response.ApiResponse;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.global.exception.LoginRequiredException;
import com.app.onestepback.global.exception.NotArtistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * REST API (v1) 컨트롤러에서 발생하는 예외를 전역으로 가로채어 일관된 JSON 형식의 에러 응답을 반환하는 어드바이스 클래스입니다.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.app.onestepback.web.api.v1")
public class GlobalRestV1ControllerAdvice {

    /**
     * 비즈니스 로직 수행 중 발생하는 사용자 정의 예외를 처리합니다.
     *
     * @param e 비즈니스 예외 객체
     * @return 에러 코드 및 메시지가 포함된 API 응답
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("[Business Error] Code: {}, Msg: {}", errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    /**
     * 요청 데이터 바인딩 및 유효성 검증 실패 시 발생하는 예외를 처리합니다.
     *
     * @param e 바인딩 예외 객체
     * @return 각 필드별 상세 검증 실패 메시지가 포함된 API 응답
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        String detailMessage = e.getBindingResult().getFieldErrors().stream()
                .map(field -> String.format("[%s: %s]", field.getField(), field.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        log.warn("[Validation Error] {}", detailMessage);

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getCode(), detailMessage));
    }

    /**
     * 잘못된 인자 또는 타입이 전달되었을 때 발생하는 예외를 처리합니다.
     *
     * @param e 잘못된 인자 예외 객체
     * @return 잘못된 데이터 타입/값에 대한 안내가 포함된 API 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("[Invalid Argument] {}", e.getMessage());

        ErrorCode errorCode = ErrorCode.INVALID_TYPE_VALUE;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getCode(), e.getMessage()));
    }

    /**
     * 인증되지 않은 사용자가 보호된 API 자원에 접근할 때 발생하는 예외를 처리합니다.
     *
     * @return 로그인 필요 상태를 알리는 API 응답
     */
    @ExceptionHandler(LoginRequiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleLoginRequiredException() {
        ErrorCode errorCode = ErrorCode.LOGIN_REQUIRED;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    /**
     * 아티스트 권한이 필요한 API 자원에 일반 사용자가 접근할 때 발생하는 예외를 처리합니다.
     *
     * @return 권한 없음 상태를 알리는 API 응답
     */
    @ExceptionHandler(NotArtistException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotArtistException() {
        ErrorCode errorCode = ErrorCode.NOT_ARTIST;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    /**
     * 명시적으로 처리되지 않은 서버 내부의 모든 예외를 일괄적으로 처리합니다.
     *
     * @param e 발생한 예외 객체
     * @return 서버 내부 오류 상태를 알리는 API 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[System Error] ", e);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }
}