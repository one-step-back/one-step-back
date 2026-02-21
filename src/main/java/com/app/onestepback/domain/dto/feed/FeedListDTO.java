package com.app.onestepback.domain.dto.feed;

import com.app.onestepback.domain.type.feed.FeedCategory;
import com.app.onestepback.domain.type.feed.FeedMediaType;
import com.app.onestepback.domain.type.feed.FeedStatus;
import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedListDTO {
    private Long id;
    private String title;
    private String content;

    private FeedMediaType mediaType;
    private FeedCategory category;
    private FeedStatus status;
    private Integer minTier;

    private Long viewCount;
    private Long likeCount;
    private Long replyCount;
    private LocalDateTime writeTime;

    private Long artistId;
    private String artistBlogName;
    private String artistNickname;
    @JsonSerialize(using = FileUrlSerializer.class)
    private String artistProfilePath;

    @JsonSerialize(using = FileUrlSerializer.class)
    private String thumbnailPath;
    private int fileCount;
    private boolean liked;

    private boolean locked; // 잠금 여부

    private Long requiredMembershipId;      // 필요한 멤버십 ID (링크 생성용)
    private String requiredMembershipName;  // 필요한 멤버십 이름 (표시용)
}