package com.app.onestepback.global.common.response;

import com.app.onestepback.global.exception.ErrorCode;
import lombok.Builder;

/**
 * REST API 통신 시 클라이언트에게 제공되는 표준 응답 규격 레코드입니다.
 * <p>
 * 비즈니스 로직의 성공 및 실패 여부(success), 요청한 실제 응답 데이터(data),
 * 예외 발생 시의 상세 오류 정보(error)를 일관된 JSON 형식으로 캡슐화합니다.
 * </p>
 *
 * @param <T> 응답 데이터의 제네릭 타입
 */
@Builder
public record ApiResponse<T>(boolean success, T data, ErrorResponse error) {

    /**
     * 클라이언트에게 반환할 데이터가 존재하지 않는 성공 응답을 생성합니다.
     *
     * @param <T> 응답 데이터 타입
     * @return 성공 상태만을 포함하는 ApiResponse 객체
     */
    public static <T> ApiResponse<T> ok() {
        return ApiResponse.<T>builder()
                .success(true)
                .data(null)
                .error(null)
                .build();
    }

    /**
     * 클라이언트에게 제공할 데이터를 포함하는 성공 응답을 생성합니다.
     *
     * @param data 클라이언트에게 전달할 비즈니스 로직 처리 결과 데이터
     * @param <T>  응답 데이터 타입
     * @return 성공 상태와 결과 데이터를 포함하는 ApiResponse 객체
     */
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .error(null)
                .build();
    }

    /**
     * 사용자 정의 오류 코드 및 상세 메시지를 포함하는 예외 실패 응답을 생성합니다.
     *
     * @param code    오류를 식별할 수 있는 고유 문자열 코드
     * @param message 발생한 오류에 대한 상세 설명 메시지
     * @param <T>     응답 데이터 타입
     * @return 실패 상태와 명시적 오류 정보를 포함하는 ApiResponse 객체
     */
    public static <T> ApiResponse<T> fail(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .data(null)
                .error(new ErrorResponse(code, message))
                .build();
    }

    /**
     * 시스템에 사전 정의된 {@link ErrorCode} 열거형을 기반으로 예외 실패 응답을 생성합니다.
     *
     * @param code 시스템 표준 규격으로 정의된 예외 코드 객체
     * @param <T>  응답 데이터 타입
     * @return 실패 상태와 시스템 표준 오류 정보를 포함하는 ApiResponse 객체
     */
    public static <T> ApiResponse<T> fail(ErrorCode code) {
        return ApiResponse.<T>builder()
                .success(false)
                .data(null)
                .error(new ErrorResponse(code.getCode(), code.getMessage()))
                .build();
    }

    /**
     * API 호출 실패 시 클라이언트가 오류의 원인을 파악할 수 있도록
     * 규격화된 오류 정보를 담는 내부 레코드입니다.
     *
     * @param code    오류 식별용 비즈니스 코드
     * @param message 사용자 또는 개발자를 위한 안내 메시지
     */
    public record ErrorResponse(String code, String message) {
    }
}