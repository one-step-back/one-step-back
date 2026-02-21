package com.app.onestepback.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 애플리케이션 내부의 비동기 작업 처리를 위한 스레드 풀(Thread Pool) 구성 클래스입니다.
 */
@Configuration
public class VariantExeConfig {

    /**
     * 시스템의 가용 프로세서(Core) 수를 기반으로 스레드 풀을 동적으로 할당하는 실행기(Executor)를 등록합니다.
     * <p>
     * CPU 점유율의 안정적인 방어를 위해 코어 수를 기준으로 기본 및 최대 스레드 크기를 제한하며,
     * 작업 대기열(Queue)이 가득 찼을 경우 거절 정책(CallerRunsPolicy)을 통해
     * 작업을 요청한 스레드가 직접 처리하도록 유도하여 시스템 장애를 방지합니다.
     * </p>
     *
     * @return 구성이 완료된 ThreadPoolTaskExecutor 인스턴스
     */
    @Bean(name = "variantExecutor")
    public ThreadPoolTaskExecutor variantExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();

        exec.setCorePoolSize(Math.max(2, cores / 2));
        exec.setMaxPoolSize(Math.max(2, cores));

        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return exec;
    }
}