package com.app.onestepback.service.paymentMethod.cmd;

/**
 * 결제 수단 등록에 필요한 최소한의 데이터를 담는 레코드입니다.
 * @param memberId 회원 고유 번호
 * @param billingKey 포트원 customer_uid
 * @param cardName 카드사 명칭
 */
public record PaymentRegisterCmd(
        Long memberId,
        String billingKey,
        String cardName,
        boolean setDefault
) {
}