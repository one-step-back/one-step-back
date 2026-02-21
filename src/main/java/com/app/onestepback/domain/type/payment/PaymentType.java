package com.app.onestepback.domain.type.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 결제 유형을 정의하는 열거형 클래스입니다.
 * INITIAL: 최초 결제, UPGRADE: 등급 변경에 따른 차액 결제, RENEWAL: 정기 갱신 결제를 나타냅니다.
 */
@Getter
@RequiredArgsConstructor
public enum PaymentType {
    INITIAL("최초 결제"),
    UPGRADE("승급 차액 결제"),
    RENEWAL("정기 갱신 결제");

    private final String description;
}