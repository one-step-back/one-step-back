package com.app.onestepback.service.file;

/**
 * 시스템에 저장된 파일의 핵심 메타데이터를 보관하는 불변 객체(Record)입니다.
 */
public record FileInfo(
        String id,
        String originalFileName,
        String fileName,
        String filePath,
        String fileExtension,
        Long fileSize
) {
}