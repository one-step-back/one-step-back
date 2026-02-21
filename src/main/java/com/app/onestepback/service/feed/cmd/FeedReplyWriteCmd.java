package com.app.onestepback.service.feed.cmd;

public record FeedReplyWriteCmd(
        Long commentId,
        Long parentMemberId,
        Long targetMemberId,
        Long feedId,
        Long feedWriterId,
        Long memberId,
        String content
) {}