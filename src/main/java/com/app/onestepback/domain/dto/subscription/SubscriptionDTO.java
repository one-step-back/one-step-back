package com.app.onestepback.domain.dto.subscription;

import com.app.onestepback.domain.type.subscribtion.SubscriptionStatus;
import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubscriptionDTO {

    public record Info(
            Long id,
            Long memberId,
            Long artistId,
            Long membershipId,
            String membershipName,
            Long price,
            SubscriptionStatus status,
            LocalDateTime nextBillingDate
    ) {
    }

    public record Receipt(
            Long id,
            String membershipName,
            Long price,
            String cardName,
            String last4Digit,
            SubscriptionStatus status,
            LocalDateTime startDate,
            LocalDateTime nextBillingDate
    ) {
    }

    public record Management(
            Long id,
            Long artistId,
            String artistNickname,
            @JsonSerialize(using = FileUrlSerializer.class)
            String artistProfilePath,
            String membershipName,
            Long price,
            SubscriptionStatus status,
            LocalDateTime startDate,
            LocalDateTime nextBillingDate,
            String nextMembershipName,
            boolean autoRenewal,

            Long paymentMethodId,
            String cardName,
            String last4Digit
    ) {
    }

    public record Renewal(
            Long id,
            Long memberId,
            Long artistId,
            Long paymentMethodId,
            String billingKey,
            Long price,
            String membershipName,
            Long nextMembershipId,
            boolean autoRenewal
    ) {
    }

    public record SubscriberList(
            Long subscriptionId,
            Long memberId,
            String nickname,
            @JsonSerialize(using = FileUrlSerializer.class)
            String profilePath,
            LocalDateTime startDate,

            Long membershipId,
            String membershipName,
            Integer levelOrder
    ) {
    }
}