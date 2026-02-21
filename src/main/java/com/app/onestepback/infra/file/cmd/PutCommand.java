package com.app.onestepback.infra.file.cmd;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * 파일 저장 요청에 필요한 데이터와 메타정보를 캡슐화한 커맨드 객체입니다.
 * <p>
 * 파일 데이터 스트림과 함께 식별자, 크기, MIME 타입 등의 필수 정보를 포함하며,
 * 작업 완료 후 입력 스트림을 안전하게 닫기 위해 {@link AutoCloseable}을 구현합니다.
 * </p>
 *
 * @param key           저장될 파일의 고유 식별자 및 경로
 * @param in            파일 데이터가 포함된 입력 스트림
 * @param contentLength 파일의 총 바이트 크기
 * @param contentType   파일의 MIME 타입 (예: "image/jpeg")
 */
@Slf4j
public record PutCommand(
        String key,
        InputStream in,
        long contentLength,
        String contentType
) implements AutoCloseable {

    /**
     * 주어진 매개변수를 바탕으로 파일 저장 커맨드 객체를 생성하는 정적 팩토리 메서드입니다.
     *
     * @param key           저장될 파일의 식별자
     * @param in            파일 입력 스트림
     * @param contentLength 파일 크기
     * @param contentType   MIME 타입
     * @return 생성된 PutCommand 인스턴스
     */
    public static PutCommand of(String key, InputStream in, long contentLength, String contentType) {
        return new PutCommand(key, in, contentLength, contentType);
    }

    /**
     * 사용이 완료된 파일 입력 스트림을 안전하게 종료합니다.
     */
    @Override
    public void close() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                log.error("입력 스트림 종료 중 오류가 발생하였습니다.", e);
            }
        }
    }
}