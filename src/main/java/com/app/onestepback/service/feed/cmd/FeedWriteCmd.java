package com.app.onestepback.service.feed.cmd;

import com.app.onestepback.domain.type.feed.FeedCategory;
import com.app.onestepback.domain.type.feed.FeedStatus;

import java.util.List;

public record FeedWriteCmd(
        Long artistId,
        String title,
        String content,
        FeedCategory category,
        FeedStatus status,
        Integer minTier,
        List<String> fileIds
) {
}