package com.app.onestepback.domain.type.etc;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    SUCCEED("결제 완료"),
    CANCELED("결제 취소");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
}
