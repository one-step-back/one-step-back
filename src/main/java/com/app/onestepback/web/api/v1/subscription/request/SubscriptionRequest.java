package com.app.onestepback.web.api.v1.subscription.request;

import jakarta.validation.constraints.NotNull;

public class SubscriptionRequest {

    // [신규 구독용]
    public record Create(
            @NotNull Long membershipId,
            @NotNull Long paymentMethodId
    ) {}

    // [업그레이드용]
    public record Upgrade(
            @NotNull Long newMembershipId,
            @NotNull Long paymentMethodId,
            @NotNull Long amount // 프론트에서 계산된 차액 (서버 검증용)
    ) {}

    // [다운그레이드용]
    public record Downgrade(
            @NotNull Long nextMembershipId
    ) {}
}