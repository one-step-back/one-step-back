package com.app.onestepback.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 카카오(Kakao) OAuth2 연동에 필요한 환경 변수를 관리하는 전역 설정 클래스입니다.
 * <p>
 * 애플리케이션 프로퍼티(application.yml)에서 주입된 클라이언트 자격 증명 정보를
 * 서비스 계층에 안전하게 제공합니다.
 * </p>
 */
@Getter
@Configuration
public class KakaoConfig {

    @Value("${oauth2.kakao.client-id}")
    private String clientId;

    @Value("${oauth2.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${oauth2.kakao.client-secret}")
    private String clientSecret;
}