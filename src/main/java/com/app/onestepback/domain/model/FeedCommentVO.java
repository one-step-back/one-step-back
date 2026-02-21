package com.app.onestepback.domain.model;

import lombok.*;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FeedCommentVO {
    private Long id;
    private Long feedId;
    private Long memberId;
    private String content;
    private int replyCount;
    private String createdTime;
    private String updatedTime;
}