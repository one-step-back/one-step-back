package com.app.onestepback.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 서버 사이드 렌더링(Thymeleaf 등) 뷰 계층에서 안전한 파일 접근을 지원하기 위해
 * 파일 경로에 보안 토큰을 명시적으로 결합해주는 유틸리티 컴포넌트입니다.
 */
@Component
@RequiredArgsConstructor
public class FileUrlSigner {

    private final FileTokenProvider tokenProvider;

    /**
     * 제공된 파일 경로에서 식별자를 추출하여 서명된 URL을 반환합니다.
     *
     * @param path 보호 대상 파일의 원본 식별 경로
     * @return 접근 토큰이 포함된 완성된 URL 경로 (예외 발생 시 원본 경로 반환)
     */
    public String sign(String path) {
        if (path == null || path.isBlank()) {
            return path;
        }

        try {
            int lastSlash = path.lastIndexOf('/');
            if (lastSlash != -1) {
                String fileId = path.substring(lastSlash + 1);
                String token = tokenProvider.generateToken(fileId);

                return path + "?token=" + token;
            }
        } catch (Exception e) {
            // 변환 과정의 예외 발생 시 원본 경로를 반환하여 렌더링 오류를 방지합니다.
        }
        return path;
    }
}