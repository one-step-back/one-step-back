package com.app.onestepback.infra.file;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * 파일의 메타데이터와 실제 바이너리 데이터를 함께 제공하는 리소스 객체입니다.
 * <p>
 * 리소스 사용이 끝난 후 메모리 누수를 방지하기 위해 반드시 스트림을 닫아야 하므로
 * {@link AutoCloseable} 인터페이스를 구현하여 자동 자원 관리를 지원합니다.
 * </p>
 *
 * @param metadata    조회된 파일의 메타정보
 * @param inputStream 실제 파일 데이터가 흐르는 입력 스트림
 */
@Slf4j
public record FileResource(
        FileObjectMetadata metadata,
        InputStream inputStream
) implements AutoCloseable {

    /**
     * 사용이 완료된 파일 입력 스트림을 안전하게 종료합니다.
     */
    @Override
    public void close() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("리소스 입력 스트림 종료 중 오류가 발생하였습니다.", e);
            }
        }
    }

    public String getContentType() {
        return metadata.contentType();
    }

    public long getContentLength() {
        return metadata.contentLength();
    }

    public String getKey() {
        return metadata.key();
    }
}