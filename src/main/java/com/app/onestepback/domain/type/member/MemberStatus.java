package com.app.onestepback.domain.type.member;

import lombok.Getter;

@Getter
public enum MemberStatus {
    ACTIVE("활성"),
    DISABLE("탈퇴"),
    SUSPENDED("정지");

    private final String displayName;

    MemberStatus(String displayName) {
        this.displayName = displayName;
    }
}
