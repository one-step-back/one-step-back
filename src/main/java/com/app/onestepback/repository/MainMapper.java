package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.main.MainPageDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MainMapper {

    /**
     * [Rising Artists] 팔로워가 가장 많은 인기 아티스트 조회 (4명)
     * - 정렬 기준: 팔로워 수 내림차순
     */
    List<MainPageDTO.RisingArtist> selectRisingArtists();

    /**
     * [Funding Spotlight] 마감이 임박한 진행 중인 펀딩 조회 (3개)
     * - 조건: 상태가 'PROCEEDING'
     * - 정렬 기준: 마감일 오름차순 (급한 순서)
     */
    List<MainPageDTO.FundingSpotlight> selectFundingSpotlight();

    /**
     * [Live Moments] 최신 공개 피드 조회 (6개)
     * - 조건: 상태가 'PUBLIC'
     * - 정렬 기준: 작성일 내림차순 (최신순)
     */
    List<MainPageDTO.LiveFeed> selectLiveFeeds();
}