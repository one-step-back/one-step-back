package com.app.onestepback.domain.model;

import com.app.onestepback.domain.type.crowdfunding.CrowdFundingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CrowdFundingVO {
    private Long id;
    private Long artistId;
    private Long writerId;
    private String title;
    private String content;
    private Long targetAmount;
    private Long currentAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private CrowdFundingStatus status;

    private String rejectReason;
    private String mainImgId;

    private Long resultFeedId;
    private LocalDateTime decisionTime;
    private LocalDateTime completedTime;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}