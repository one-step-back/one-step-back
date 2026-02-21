package com.app.onestepback.domain.dto.payment;

import java.time.LocalDateTime;

/**
 * 펀딩 결제 내역 관련 DTO
 */
public class FundingPaymentDTO {
    public record History(
            Long paymentId,
            Long fundingId,
            Long artistId,
            String artistName,
            String title,
            Long amount,
            String status,
            LocalDateTime paymentDate
    ) {
    }
}