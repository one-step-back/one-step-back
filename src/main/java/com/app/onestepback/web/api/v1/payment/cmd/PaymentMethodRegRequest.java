package com.app.onestepback.web.api.v1.payment.cmd;

import static com.app.onestepback.global.util.Stringx.trimToNull;

/**
 * 결제 수단 등록 요청을 위한 DTO입니다.
 */
public record PaymentMethodRegRequest(
        String billingKey,
        String cardName
) {
    public PaymentMethodRegRequest {
        billingKey = trimToNull(billingKey);
        cardName = trimToNull(cardName);
    }
}