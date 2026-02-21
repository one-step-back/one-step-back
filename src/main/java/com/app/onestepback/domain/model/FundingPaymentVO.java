package com.app.onestepback.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FundingPaymentVO {
    private Long id;
    private Long fundingId;
    private Long memberId;
    private String merchantUid;
    private String impUid;
    private Long amount;
    private String status;
    private LocalDateTime paymentDate;
}