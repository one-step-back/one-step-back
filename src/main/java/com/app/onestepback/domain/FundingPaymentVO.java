package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class FundingPaymentVO {
//    펀딩 결제 id
    private Long id;
//    크라우드 펀딩
    private Long fundingId;
//    회원 번호
    private Long memberId;
//    결제 금액
    private Number fundingAmountMoney;
}
