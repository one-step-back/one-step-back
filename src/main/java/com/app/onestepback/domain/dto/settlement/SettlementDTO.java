package com.app.onestepback.domain.dto.settlement;

import com.app.onestepback.domain.type.settlement.SettlementStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SettlementDTO {

    // 1. 정산 요약 (카드 상단 표시용)
    public record Summary(
            Long artistId,
            Long totalSales,          // 누적 미정산 매출 (총액)
            Long feeAmount,           // 수수료 (5%)
            Long distributableAmount, // 출금 가능액 (Net Income)
            Long completedSettlementAmount // (옵션) 이미 정산받은 총 누적액
    ) {
    }

    // 2. 월별 매출 통계 (그래프용 데이터)
    public record MonthlyRevenue(
            String month, // "2026-01"
            Long amount   // 해당 월 매출
    ) {
    }

    // 3. 대시보드 조회용 통합 DTO
    public record Dashboard(
            Summary summary,
            List<MonthlyRevenue> monthlyRevenues
    ) {
    }

    public record History(
            Long id,
            Long totalAmount,          // 총 매출
            Long feeAmount,            // 수수료
            Long netAmount,            // 실수령액
            SettlementStatus status,   // 상태 (REQUESTED, COMPLETED, REJECTED)
            LocalDateTime requestTime, // 신청일
            LocalDateTime completeTime // 지급/반려일
    ) {}
}