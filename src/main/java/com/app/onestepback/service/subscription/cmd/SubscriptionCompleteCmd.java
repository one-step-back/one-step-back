package com.app.onestepback.service.subscription.cmd;

public record SubscriptionCompleteCmd(
        Long memberId,
        Long membershipId,
        String impUid,
        String customerUid
) {
}
