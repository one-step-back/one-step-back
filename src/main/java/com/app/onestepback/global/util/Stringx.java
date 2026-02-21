package com.app.onestepback.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 애플리케이션 전반에서 사용되는 문자열(String) 데이터의
 * 정제 및 공통 처리 로직을 제공하는 유틸리티 클래스입니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Stringx {

    /**
     * 입력된 문자열의 좌우 공백을 제거하며, 결과가 빈 문자열일 경우 null을 반환합니다.
     *
     * @param str 처리 대상 원본 문자열
     * @return 공백이 정제된 문자열 또는 데이터가 존재하지 않을 경우 null
     */
    public static String trimToNull(String str) {
        if (str == null) return null;
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}