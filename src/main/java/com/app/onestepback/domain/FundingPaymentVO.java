package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class FundingPaymentVO {
    private Long id;
    private Long fundingId;
    private Long memberId;
    private Number fundingAmountMoney;
}
