package com.app.onestepback.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 외부 HTTP API 통신을 위한 클라이언트 객체를 구성하는 클래스입니다.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 동기식 HTTP 요청을 수행하기 위한 RestTemplate 객체를 생성하여 스프링 빈으로 등록합니다.
     *
     * @return RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}