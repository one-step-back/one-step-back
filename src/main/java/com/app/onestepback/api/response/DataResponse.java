package com.app.onestepback.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class DataResponse<T> {
    private boolean success;
    private String message;
    private T data;

    @Builder
    public DataResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
