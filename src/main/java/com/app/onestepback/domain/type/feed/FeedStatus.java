package com.app.onestepback.domain.type.feed;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeedStatus {
    PUBLIC("전체 공개"),
    MEMBER_ONLY("멤버십 전용"),
    PRIVATE("나만 보기"),
    DRAFT("임시 저장");

    private final String description;
}