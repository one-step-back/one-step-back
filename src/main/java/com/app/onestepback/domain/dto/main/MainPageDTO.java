package com.app.onestepback.domain.dto.main;

import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메인 페이지(인덱스) 구성을 위한 통합 데이터 전송 객체
 */
public class MainPageDTO {

    /**
     * 메인 화면 전체 데이터를 담는 래퍼(Wrapper)
     */
    @Builder
    public record Info(
            List<RisingArtist> risingArtists,
            List<FundingSpotlight> fundings,
            List<LiveFeed> feeds
    ) {
    }

    /**
     * 1. Rising Artists (급상승 아티스트)
     */
    public record RisingArtist(
            Long id,
            String name,
            String description,
            Long followerCount,
            @JsonSerialize(using = FileUrlSerializer.class)
            String profilePath
    ) {
    }

    /**
     * 2. Funding Spotlight (주목할 만한 펀딩)
     */
    public record FundingSpotlight(
            Long id,
            Long artistId,
            String title,
            String artistName,
            Integer achieveRate,
            Long daysLeft,
            @JsonSerialize(using = FileUrlSerializer.class)
            String thumbnailPath
    ) {
    }

    /**
     * 3. Live Moments (실시간 피드 맛보기)
     */
    public record LiveFeed(
            Long id,
            Long artistId,
            String artistName,
            @JsonSerialize(using = FileUrlSerializer.class)
            String artistProfilePath,
            String contentPreview,
            LocalDateTime writeTime
    ) {
    }
}