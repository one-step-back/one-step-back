package com.app.onestepback.domain.model;

import com.app.onestepback.domain.type.subscribtion.SubscriptionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SubscriptionVO {
    private Long id;
    private Long memberId;
    private Long artistId;
    private Long membershipId;

    private Long paymentMethodId;
    private Long nextMembershipId;

    private String lastMerchantUid;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime nextBillingDate;
    private LocalDateTime updatedTime;

    private Boolean autoRenewal;
}