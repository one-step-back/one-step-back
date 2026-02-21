package com.app.onestepback.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PaymentMethodVO {
    private Long id;
    private Long memberId;
    private String billingKey;
    private String last4Digit;
    private String cardName;
    private boolean isDefault;
    private LocalDateTime createdTime;
}