package com.app.onestepback.domain.model;

import com.app.onestepback.domain.type.settlement.SettlementStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SettlementVO {
    private Long id;
    private Long artistId;

    private Long totalAmount;  // 총 매출액
    private Long feeAmount;    // 수수료 (5%)
    private Long netAmount;    // 실 수령액 (95%)

    private SettlementStatus status;     // REQUESTED, COMPLETED, REJECTED

    private LocalDateTime requestTime;  // 정산 신청일
    private LocalDateTime completeTime; // 정산 지급일
}