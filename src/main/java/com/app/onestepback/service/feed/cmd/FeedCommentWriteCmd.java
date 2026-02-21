package com.app.onestepback.service.feed.cmd;

public record FeedCommentWriteCmd(
        Long feedId,
        Long writerId,
        Long memberId,
        String content
) {
}
