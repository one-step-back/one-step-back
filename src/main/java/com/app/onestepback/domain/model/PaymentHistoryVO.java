package com.app.onestepback.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PaymentHistoryVO {
    private Long id;
    private Long memberId;
    private Long subscriptionId; // Nullable (구독 결제가 아닌 단건 결제일 수도 있으니)
    private String impUid;       // 포트원 고유 번호
    private String merchantUid;  // 주문 번호
    private Long amount;
    private String paymentType;  // SUBSCRIPTION, DONATION 등
    private String status;       // PAID, FAILED, CANCELLED
    private LocalDateTime createdTime;
}