package com.app.onestepback.cron;

import com.app.onestepback.domain.dto.subscription.SubscriptionDTO;
import com.app.onestepback.repository.SubscriptionMapper;
import com.app.onestepback.service.subscription.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 멤버십 구독 자동 갱신 및 결제를 수행하는 스케줄러 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionMapper subscriptionMapper;
    private final SubscriptionService subscriptionService;

    /**
     * 매일 자정에 실행되어 결제 예정일이 도래한 구독 건에 대해 자동 결제를 요청합니다.
     * 결제 실패 시 해당 구독의 상태를 변경하여 서비스 접근을 제한합니다.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void runDailyRenewal() {
        log.info("[Cron] 멤버십 자동 결제 스케줄러 시작...");

        List<SubscriptionDTO.Renewal> targets = subscriptionMapper.selectSubscriptionsForRenewal();

        if (targets.isEmpty()) {
            log.info("[Cron] 금일 자동 결제 갱신 대상이 없습니다. 작업을 종료합니다.");
            return;
        }

        log.info("[Cron] 갱신 대상: {}건", targets.size());

        int successCount = 0;
        int failCount = 0;

        for (SubscriptionDTO.Renewal sub : targets) {
            try {
                // 서비스 내부에서 트랜잭션을 분리하여 개별 결제를 안전하게 처리합니다.
                subscriptionService.renewSubscription(sub);
                successCount++;
            } catch (Exception e) {
                log.error("[Cron] 구독 갱신 결제 실패 - SubscriptionID: {}, 사유: {}", sub.id(), e.getMessage());
                try {
                    subscriptionService.handleRenewalFailure(sub);
                } catch (Exception failEx) {
                    log.error("[Cron] 구독 실패 상태 업데이트 중 오류 발생: {}", failEx.getMessage());
                }
                failCount++;
            }
        }

        log.info("[Cron] 멤버십 자동 결제 스케줄러 종료. 성공: {}건, 실패: {}건", successCount, failCount);
    }
}