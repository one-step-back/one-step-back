package com.app.onestepback.domain.dto.feed.comment;

import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedCommentDTO {
    private Long id;
    private Long feedId;
    private Long memberId;
    private String content;
    private int replyCount;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    private String memberNickname;
    @JsonSerialize(using = FileUrlSerializer.class)
    private String memberProfile;
}
