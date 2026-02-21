package com.app.onestepback.domain.dto.feed;

import com.app.onestepback.domain.type.feed.FeedCategory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedSearchCond {
    private Long artistId;      // 특정 아티스트 필터
    private FeedCategory category; // 카테고리 필터
    private Long viewerId;      // 조회자 ID (좋아요 여부 확인용)

    private Long lastFeedId;    // 커서 (마지막 피드 ID, 첫 요청 시 null)
    private int size;           // 가져올 개수

    private boolean isOwner;    // 본인 여부
}