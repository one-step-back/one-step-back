package com.app.onestepback.domain.dto.feed;

import com.app.onestepback.domain.type.feed.FeedCategory;
import com.app.onestepback.domain.type.feed.FeedMediaType;
import com.app.onestepback.domain.type.feed.FeedStatus;
import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class FeedDetailDTO {
    private Long id;
    private String title;
    private String content;
    private FeedMediaType mediaType;
    private FeedCategory category;
    private FeedStatus status;

    private Integer minTier;
    private boolean isLocked;

    private Long viewCount;
    private Long likeCount;
    private Long replyCount;
    private LocalDateTime writeTime;

    private Long artistId;
    private String artistBlogName;
    private String artistNickname;
    @JsonSerialize(using = FileUrlSerializer.class)
    private String artistProfilePath;

    private boolean liked;

    private Long requiredMembershipId;
    private String requiredMembershipName;

    private List<FeedFileDTO> files = new ArrayList<>();
}