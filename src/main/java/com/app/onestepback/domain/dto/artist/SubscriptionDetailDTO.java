package com.app.onestepback.domain.dto.artist;

import com.app.onestepback.domain.type.subscribtion.SubscriptionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionDetailDTO {
    private Long id;                // 내 구독 ID
    private String membershipName;  // VIP, GOLD 등
    private SubscriptionStatus status; 
    private LocalDateTime nextBillingDate;
}
