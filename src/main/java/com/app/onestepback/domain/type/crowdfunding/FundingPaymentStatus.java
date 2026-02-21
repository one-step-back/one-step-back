package com.app.onestepback.domain.type.crowdfunding;

public enum FundingPaymentStatus {
    PAID,       // 결제 완료 (예수금 보관 상태)
    REFUNDED,   // 환불 완료
    CANCELLED,  // 결제 취소
    MOVED       // 정산 테이블로 이관됨 (펀딩 성공)
}