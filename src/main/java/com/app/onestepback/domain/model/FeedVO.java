package com.app.onestepback.domain.model;

import com.app.onestepback.domain.type.feed.FeedCategory;
import com.app.onestepback.domain.type.feed.FeedMediaType;
import com.app.onestepback.domain.type.feed.FeedStatus;
import lombok.*;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FeedVO {
    private Long id;
    private Long artistId;

    @Nullable
    private String feedTitle;

    private String feedContent;

    @Builder.Default
    private FeedMediaType feedMediaType = FeedMediaType.TEXT;

    private FeedCategory feedCategory;

    @Builder.Default
    private Long feedViewCount = 0L;

    @Builder.Default
    private Long feedLikeCount = 0L;

    @Builder.Default
    private Long feedReplyCount = 0L;

    @Builder.Default
    private FeedStatus feedStatus = FeedStatus.PUBLIC;

    // 멤버십 전용 게시글일 경우 요구되는 최소 티어 (0: 전체, 1~3: 티어)
    @Builder.Default
    private Integer feedMinTier = 0;

    private LocalDateTime feedWriteTime;

    @Nullable
    private LocalDateTime feedUpdateTime;
}