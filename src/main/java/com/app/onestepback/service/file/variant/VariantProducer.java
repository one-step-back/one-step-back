package com.app.onestepback.service.file.variant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 미디어 변환 작업(Variant Job)을 비동기 스레드 풀(TaskExecutor)에 위임하여 발행하는 퍼블리셔 컴포넌트입니다.
 * <p>
 * 메인 스레드의 응답성을 보장하기 위해 실제 변환 처리는 별도의 작업 스레드에서 수행되며,
 * 작업 중 발생하는 예외는 내부에서 캡슐화 및 로깅 처리되어 메인 비즈니스 흐름에 영향을 주지 않습니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VariantProducer {

    private final TaskExecutor variantExecutor;
    private final VariantWorker worker;

    /**
     * 비동기 미디어 변환 작업을 스레드 풀에 발행합니다.
     *
     * @param job 변환할 대상과 작업 유형이 포함된 작업 객체
     */
    public void publish(VariantJob job) {
        variantExecutor.execute(() -> {
            try {
                worker.handle(job);
            } catch (Exception e) {
                log.error("[Variant Processing] 비동기 미디어 변환 작업 중 시스템 오류가 발생하였습니다. Target File ID: {}, Error: {}", job.fileId(), e.getMessage());
            }
        });
    }
}