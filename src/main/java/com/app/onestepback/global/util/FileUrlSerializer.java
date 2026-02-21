package com.app.onestepback.global.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * REST API 응답 시 파일 경로 필드에 보안 접근 제어 토큰을 동적으로 부착하는 Jackson 시리얼라이저입니다.
 * <p>
 * 보호된 API 경로를 대상으로만 작동하며, 추출된 파일 ID를 기반으로 서명을 발급하여
 * 쿼리 파라미터 형태로 URL에 결합합니다. 정적 자원 경로는 변환 없이 원본을 유지합니다.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class FileUrlSerializer extends JsonSerializer<String> {

    private final FileTokenProvider tokenProvider;
    private static final String FILE_API_PREFIX = "/api/v1/files/";

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        if (!value.startsWith(FILE_API_PREFIX)) {
            gen.writeString(value);
            return;
        }

        try {
            String fileId = value.substring(FILE_API_PREFIX.length());

            if (!fileId.isBlank()) {
                String token = tokenProvider.generateToken(fileId);
                String signedUrl = value + "?token=" + token;
                gen.writeString(signedUrl);
            } else {
                gen.writeString(value);
            }
        } catch (Exception e) {
            gen.writeString(value);
        }
    }
}