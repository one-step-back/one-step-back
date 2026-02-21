/**
 * @file api.ts
 * @package types
 * @description 애플리케이션 전반에서 공통으로 사용되는 REST API 응답 규격을 정의하는 모듈입니다.
 */

/**
 * @interface ApiErrorResponse
 * @description 서버로부터 반환되는 비즈니스 로직 및 검증 에러의 표준 구조입니다.
 */
export interface ApiErrorResponse {
    code: string;
    message: string;
}

/**
 * @interface ApiResponse
 * @description V1 API에서 공통으로 반환하는 최상위 래퍼(Wrapper) 객체 구조입니다.
 * @template T - 반환되는 데이터의 제네릭 타입 (기본값: any)
 */
export interface ApiResponse<T = any> {
    success: boolean;
    data?: T;
    error?: ApiErrorResponse;
}

/**
 * @interface SliceResponse
 * @description 무한 스크롤 및 페이징 처리를 위한 Spring Data Slice 규격의 래퍼 객체 구조입니다.
 * @template T - 배열에 포함될 개별 데이터의 제네릭 타입
 */
export interface SliceResponse<T> {
    content: T[];
    hasNext: boolean;
}