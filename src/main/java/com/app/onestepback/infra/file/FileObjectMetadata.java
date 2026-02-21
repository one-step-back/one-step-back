package com.app.onestepback.infra.file;

import java.time.Instant;

/**
 * 스토리지에 저장된 파일의 핵심 메타데이터를 담고 있는 불변 객체입니다.
 *
 * @param key          저장소 내 파일의 고유 식별 경로
 * @param contentType  파일의 미디어 타입 (MIME Type)
 * @param contentLength 파일의 총 크기 (Byte 단위)
 * @param lastModified 마지막으로 수정된 시간
 */
public record FileObjectMetadata(
        String key,
        String contentType,
        long contentLength,
        Instant lastModified
) {
    /**
     * 메타데이터 객체를 생성하기 위한 정적 팩토리 메서드입니다.
     *
     * @param key          파일 식별자
     * @param contentType  MIME 타입
     * @param contentLength 파일 크기
     * @param lastModified 수정 일시
     * @return 생성된 FileObjectMetadata 인스턴스
     */
    public static FileObjectMetadata of(String key, String contentType, long contentLength, Instant lastModified) {
        return new FileObjectMetadata(key, contentType, contentLength, lastModified);
    }
}