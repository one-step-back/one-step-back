package com.app.onestepback.cron;

import com.app.onestepback.domain.model.SettlementVO;
import com.app.onestepback.service.settlement.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 아티스트의 수익 정산 요청을 처리하는 스케줄러 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementScheduler {

    private final SettlementService settlementService;

    /**
     * 매일 새벽 4시에 실행되어 접수된 정산 요청을 순차적으로 처리합니다.
     * 처리 중 오류 발생 시 다른 정산 건에 영향을 주지 않도록 개별 예외 처리를 수행합니다.
     */
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void runDailySettlement() {
        log.info("[Cron] 정산 지급 스케줄러 시작...");

        List<SettlementVO> targets = settlementService.findRequestedSettlements();

        if (targets.isEmpty()) {
            log.info("[Cron] 정산 대상이 존재하지 않습니다. 작업을 종료합니다.");
            return;
        }

        log.info("[Cron] 정산 대상: {}건", targets.size());

        int successCount = 0;
        int failCount = 0;

        for (SettlementVO settlement : targets) {
            try {
                // 개별 정산 건에 대해 독립적인 트랜잭션 처리를 수행합니다.
                settlementService.processSettlement(settlement);
                successCount++;
            } catch (Exception e) {
                log.error("[Cron] 정산 처리 실패 - SettlementID: {}, 사유: {}", settlement.getId(), e.getMessage());

                try {
                    // 정산 실패 시 상태를 반려 또는 에러 상태로 변경하기 위한 독립 트랜잭션을 호출합니다.
                    settlementService.handleSettlementFailure(settlement, e.getMessage());
                } catch (Exception failEx) {
                    log.error("[Cron] 정산 실패 상태 업데이트 중 치명적 오류 발생: {}", failEx.getMessage());
                }
                failCount++;
            }
        }

        log.info("[Cron] 정산 스케줄러 종료. 성공: {}건, 실패: {}건", successCount, failCount);
    }
}