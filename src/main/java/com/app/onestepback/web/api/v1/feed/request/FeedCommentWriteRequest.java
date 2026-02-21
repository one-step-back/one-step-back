package com.app.onestepback.web.api.v1.feed.request;

import com.app.onestepback.service.feed.cmd.FeedCommentWriteCmd;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static com.app.onestepback.global.util.Stringx.trimToNull;

public record FeedCommentWriteRequest(
        @NotNull(message = "피드 ID는 필수입니다.")
        Long feedId,

        Long writerId,

        @NotBlank(message = "댓글 내용을 입력해주세요.")
        @Size(max = 1000, message = "댓글은 1000자 이내로 작성해주세요.")
        String content
) {
    public FeedCommentWriteRequest {
        content = trimToNull(content);
    }

    public FeedCommentWriteCmd toCmd(Long memberId) {
        return new FeedCommentWriteCmd(
                feedId,
                writerId,
                memberId,
                content
        );
    }
}