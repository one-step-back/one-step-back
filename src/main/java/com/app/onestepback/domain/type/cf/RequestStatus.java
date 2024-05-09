package com.app.onestepback.domain.type.cf;

import lombok.Getter;

@Getter
public enum RequestStatus {
    REQUESTED("요청"),
    REJECTED("거절"),
    ACCEPTED("수락");

    private final String displayName;

    RequestStatus(String displayName) {
        this.displayName = displayName;
    }
}
