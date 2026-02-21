package com.app.onestepback.cron;

import com.app.onestepback.domain.model.CrowdFundingVO;
import com.app.onestepback.service.crowdfunding.CrowdFundingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 크라우드 펀딩과 관련된 자동화 상태 변경 작업을 수행하는 스케줄러 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CrowdFundingScheduler {

    private final CrowdFundingService crowdFundingService;

    /**
     * 매일 자정 5분에 실행되어, 종료 일자가 지난 진행 중인 펀딩의 상태를 '종료'로 일괄 변경합니다.
     */
    @Scheduled(cron = "0 5 0 * * *", zone = "Asia/Seoul")
    public void runFundingExpirationJob() {
        log.info("[Cron] 기한 만료 펀딩 종료 스케줄러 시작...");
        try {
            int updatedCount = crowdFundingService.updateExpiredFundings();
            log.info("[Cron] 기한 만료 펀딩 종료 처리 완료. 대상: {}건", updatedCount);
        } catch (Exception e) {
            log.error("[Cron] 기한 만료 펀딩 종료 처리 중 예외 발생: {}", e.getMessage());
        }
    }

    /**
     * 매일 자정 10분에 실행되어, 종료 후 7일이 경과하도록 아티스트가 결과를 결정하지 않은 펀딩을
     * 자동으로 실패 처리하고 참여자들에게 환불을 진행합니다.
     */
    @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
    public void runFundingAutoFailJob() {
        log.info("[Cron] 방치된 종료 펀딩 자동 실패/환불 스케줄러 시작...");

        List<CrowdFundingVO> targets = crowdFundingService.getExpiredEndedFundings();

        if (targets.isEmpty()) {
            log.info("[Cron] 자동 환불 대상 없음. 작업을 종료합니다.");
            return;
        }

        log.info("[Cron] 자동 환불 대상: {}건", targets.size());

        for (CrowdFundingVO funding : targets) {
            try {
                crowdFundingService.fail(funding.getId(), funding.getArtistId(), "아티스트 결정 기한 만료로 인한 자동 환불 처리");
            } catch (Exception e) {
                log.error("[Cron] 펀딩 자동 환불 실패 - FundingID: {}, 사유: {}", funding.getId(), e.getMessage());
            }
        }
    }
}