package com.app.onestepback.service.file.variant;

import lombok.Builder;

/**
 * 비동기 미디어 파일 변환 작업(모자이크, 썸네일 등)에 필요한 데이터를 캡슐화한 레코드입니다.
 *
 * @param fileId        변환 대상이 되는 원본 파일의 고유 식별자
 * @param originalPath  원본 파일이 저장된 스토리지 경로 (예: yyyy/MM/dd)
 * @param savedFileName 원본 파일의 실제 저장 파일명 (예: 01KEX..._name.jpg)
 * @param type          수행할 변환 작업의 유형
 */
@Builder
public record VariantJob(
        String fileId,
        String originalPath,
        String savedFileName,
        VariantType type
) {
    /**
     * 미디어 변환 작업의 유형을 정의하는 열거형입니다.
     */
    public enum VariantType {
        MOSAIC, THUMBNAIL
    }
}