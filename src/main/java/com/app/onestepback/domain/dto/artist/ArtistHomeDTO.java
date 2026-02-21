package com.app.onestepback.domain.dto.artist;

import com.app.onestepback.domain.type.crowdfunding.CrowdFundingStatus;
import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 아티스트 홈 화면 통합 데이터 전송 객체
 */
public class ArtistHomeDTO {

    /**
     * [Wrapper] 홈 화면 전체 데이터를 담는 메인 레코드
     */
    @Builder
    public record Info(
            FundingHighlight funding,       // 진행 중인 메인 펀딩 (없으면 null)
            List<RecentFeed> feeds,         // 최신 피드 리스트 (최대 3~5개)
            List<MembershipInfo> memberships // 멤버십 홍보 리스트
    ) {
    }

    /**
     * 1. Ongoing Project (펀딩 하이라이트)
     */
    public record FundingHighlight(
            Long id,
            String title,
            @JsonSerialize(using = FileUrlSerializer.class)
            String mainImgPath,
            Integer achieveRate,    // 달성률 (%)
            Long daysLeft,          // 남은 기간 (D-Day)
            CrowdFundingStatus status           // PROCEEDING, UPCOMING 등
    ) {
    }

    /**
     * 2. Recent Feeds (최신 피드)
     */
    public record RecentFeed(
            Long id,
            String title,
            String contentPreview,
            String mediaType,
            Integer minTier,
            LocalDateTime writeTime,
            Long likeCount,
            Long replyCount
    ) {
    }

    /**
     * 3. Membership Promotion (멤버십 정보)
     */
    public record MembershipInfo(
            Long id,
            String name,
            Long price,
            String description,
            @JsonSerialize(using = FileUrlSerializer.class)
            String imagePath,
            Integer level
    ) {
    }
}