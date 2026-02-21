package com.app.onestepback.service.file.cmd;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * 스토리지 및 데이터베이스에 파일을 저장하기 위한 커맨드 객체입니다.
 * <p>
 * 메모리 자원 낭비를 방지하고 유연성을 확보하기 위해
 * 실제 파일 데이터(InputStream)는 지연 평가(Lazy Evaluation)를 위한 Supplier 형태로 제공받습니다.
 * </p>
 */
public record SaveFileCmd(
        long ownerId,
        Long targetId,
        String targetGb,
        String originalFilename,
        String contentType,
        long contentLength,
        Supplier<InputStream> content
) {
}