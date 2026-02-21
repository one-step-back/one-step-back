package com.app.onestepback.domain.type.feed;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeedCategory {
    WORK("작품"),
    DAILY("일상"),
    NOTICE("공지사항");

    private final String description;
}