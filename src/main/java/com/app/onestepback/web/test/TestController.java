package com.app.onestepback.web.test;

import com.app.onestepback.cron.CrowdFundingScheduler;
import com.app.onestepback.cron.SettlementScheduler;
import com.app.onestepback.cron.SubscriptionScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final SubscriptionScheduler subscriptionScheduler;
    private final SettlementScheduler settlementScheduler;
    private final CrowdFundingScheduler crowdFundingScheduler;

    /**
     * 수동으로 정기 결제(구독 갱신) 배치 스케줄러를 즉시 실행합니다.
     */
    @GetMapping("/trigger-renewal")
    public String triggerRenewal() {
        subscriptionScheduler.runDailyRenewal();
        return "리뉴얼 정상 처리됨.";
    }

    /**
     * 수동으로 아티스트 수익 일일 정산 배치 스케줄러를 즉시 실행합니다.
     */
    @GetMapping("/trigger-settlement")
    public String triggerSettlement() {
        settlementScheduler.runDailySettlement();
        return "정산 처리 완료.";
    }

    /**
     * 수동으로 기한이 만료된 펀딩을 일괄 종료 처리하는 배치 스케줄러를 즉시 실행합니다.
     */
    @GetMapping("/trigger-funding-expiration")
    public String triggerFundingExpiration() {
        crowdFundingScheduler.runFundingExpirationJob();
        return "기한 만료 펀딩 일괄 종료 처리 완료.";
    }

    /**
     * 수동으로 장기 방치된 펀딩을 실패(환불) 처리하는 배치 스케줄러를 즉시 실행합니다.
     */
    @GetMapping("/trigger-funding-autofail")
    public String triggerFundingAutoFail() {
        crowdFundingScheduler.runFundingAutoFailJob();
        return "방치된 펀딩 자동 환불 스케줄러 강제 실행 완료.";
    }
}