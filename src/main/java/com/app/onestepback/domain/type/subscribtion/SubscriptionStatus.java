package com.app.onestepback.domain.type.subscribtion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionStatus {
    ACTIVE("구독 중"),
    CANCELLED("구독 취소"),
    EXPIRED("기간 만료"),
    FAILED("결제 실패");

    private final String description;
}