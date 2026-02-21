package com.app.onestepback.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 포트원(PortOne) 외부 결제 API 통신에 필요한 인증 및 구성 정보를 관리하는 클래스입니다.
 * <p>
 * 환경 설정 파일(application.yml 등)에서 주입된 민감한 API 자격 증명 데이터를 보관하며,
 * 외부 통신 클라이언트 모듈에서 이를 안전하게 참조할 수 있도록 제공합니다.
 * </p>
 */
@Getter
@Configuration
public class PortOneConfig {

    @Value("${port-one.api.merchant-id}")
    private String merchantId;

    @Value("${port-one.api.url}")
    private String apiUrl;

    @Value("${port-one.api.key}")
    private String apiKey;

    @Value("${port-one.api.secret}")
    private String apiSecret;
}