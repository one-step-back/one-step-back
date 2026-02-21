package com.app.onestepback.web.api.v1.feed.request;

import com.app.onestepback.service.feed.cmd.FeedReplyWriteCmd;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static com.app.onestepback.global.util.Stringx.trimToNull;

public record FeedReplyWriteRequest(
        @NotNull(message = "부모 댓글 ID는 필수입니다.")
        Long commentId,

        @NotNull(message = "부모 댓글 작성자 ID는 필수입니다.")
        Long parentMemberId,

        Long targetMemberId,

        @NotNull(message = "피드 ID는 필수입니다.")
        Long feedId,

        @NotNull(message = "피드 작성자 ID는 필수입니다.")
        Long feedWriterId,

        @NotBlank(message = "내용을 입력해주세요.")
        @Size(max = 1000, message = "답글은 1000자 이내로 작성해주세요.")
        String content
) {
    public FeedReplyWriteRequest {
        content = trimToNull(content);
    }

    public FeedReplyWriteCmd toCmd(Long memberId) {
        return new FeedReplyWriteCmd(
                commentId,
                parentMemberId,
                targetMemberId,
                feedId,
                feedWriterId,
                memberId,
                content
        );
    }
}