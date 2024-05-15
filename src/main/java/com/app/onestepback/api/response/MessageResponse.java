package com.app.onestepback.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class MessageResponse {
    private boolean success;
    private String message;

    @Builder
    public MessageResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
