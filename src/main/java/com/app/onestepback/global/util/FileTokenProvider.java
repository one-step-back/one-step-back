package com.app.onestepback.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 파일 접근 제어를 위한 서명된 URL(Signed URL) 생성 및 검증을 담당하는 유틸리티 컴포넌트입니다.
 * <p>
 * 시간 기반 토큰(Time-Based Token) 알고리즘을 적용하여 특정 시간 윈도우(Time Window) 동안
 * 유효한 서명을 생성합니다. 이를 통해 보안을 유지하면서도 브라우저의 캐싱 효율을 극대화합니다.
 * </p>
 */
@Component
public class FileTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long WINDOW_MS = 1000 * 60 * 60;

    /**
     * 특정 파일 ID에 대하여 만료 시간과 HMAC-SHA256 서명이 결합된 보안 토큰을 생성합니다.
     *
     * @param fileId 보안 토큰을 발급할 대상 파일의 고유 식별자
     * @return Base64(URL-Safe) 형식으로 인코딩된 토큰 문자열
     */
    public String generateToken(String fileId) {
        long now = System.currentTimeMillis();
        long windowStart = (now / WINDOW_MS) * WINDOW_MS;
        long expiry = windowStart + (WINDOW_MS * 2);

        String data = fileId + "." + expiry;
        String signature = sign(data);

        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString((expiry + "." + signature).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 클라이언트로부터 전달받은 토큰의 유효성, 만료 여부 및 무결성을 검증합니다.
     *
     * @param fileId 검증 대상 파일의 고유 식별자
     * @param token  클라이언트가 요청 시 제공한 토큰 문자열
     * @return 토큰이 유효할 경우 true, 위변조되었거나 만료된 경우 false 반환
     */
    public boolean validateToken(String fileId, String token) {
        try {
            if (token == null || token.isBlank()) return false;

            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\.");

            if (parts.length != 2) return false;

            long expiry = Long.parseLong(parts[0]);
            String receivedSignature = parts[1];

            if (System.currentTimeMillis() > expiry) {
                return false;
            }

            String expectedSignature = sign(fileId + "." + expiry);
            return expectedSignature.equals(receivedSignature);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 내부 데이터를 기반으로 HMAC-SHA256 해시 서명을 생성합니다.
     *
     * @param data 서명할 대상 데이터 문자열
     * @return Base64로 인코딩된 서명 결과값
     */
    private String sign(String data) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKeySpec);
            byte[] hash = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("서명 생성 처리 중 시스템 오류가 발생하였습니다.", e);
        }
    }
}