package com.app.onestepback.domain.type.cf;

import lombok.Getter;

@Getter
public enum FundingStatus {
    ONGOING("진행중"),
    BREAK("중단됨"),
    FINISH("종료됨");

    private final String displayName;

    FundingStatus(String displayName) {
        this.displayName = displayName;
    }
}
