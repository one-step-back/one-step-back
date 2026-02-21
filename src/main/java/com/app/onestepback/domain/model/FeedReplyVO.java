package com.app.onestepback.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FeedReplyVO {
    private Long id;
    private Long commentId;
    private Long memberId;
    private Long targetMemberId;
    private String content;
    private String isDeleted;
    private LocalDateTime deletedTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}