package com.app.onestepback.domain.dto.feed.comment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedCommentSearchCond {
    private Long feedId;
    private Long lastCommentId;
    private int size;
}