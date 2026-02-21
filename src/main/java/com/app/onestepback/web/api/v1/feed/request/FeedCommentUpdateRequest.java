package com.app.onestepback.web.api.v1.feed.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.app.onestepback.global.util.Stringx.trimToNull;

public record FeedCommentUpdateRequest(
        @NotBlank(message = "수정할 내용을 입력해주세요.")
        @Size(max = 1000, message = "댓글은 1000자 이내로 작성해주세요.")
        String content
) {
    public FeedCommentUpdateRequest {
        content = trimToNull(content);
    }
}