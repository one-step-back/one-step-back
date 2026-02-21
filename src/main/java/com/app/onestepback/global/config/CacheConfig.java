package com.app.onestepback.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 애플리케이션의 캐시(Cache) 설정을 담당하는 전역 구성 클래스입니다.
 * <p>
 * Caffeine 캐시 라이브러리를 사용하여 메모리 기반의 고성능 로컬 캐싱을 구성하며,
 * 애플리케이션의 전반적인 응답 속도 향상과 데이터베이스 부하 감소를 목적으로 합니다.
 * </p>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 시스템에서 사용할 기본 캐시 매니저를 생성하여 스프링 빈으로 등록합니다.
     * <p>
     * 메모리 누수를 방지하기 위해 최대 저장 항목 수와
     * 쓰기 작업 이후의 만료 시간을 명시적으로 설정합니다.
     * </p>
     *
     * @return 구성이 완료된 CaffeineCacheManager 인스턴스
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(3, TimeUnit.MINUTES));

        return cacheManager;
    }
}