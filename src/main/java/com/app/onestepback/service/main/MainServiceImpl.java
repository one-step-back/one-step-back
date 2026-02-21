package com.app.onestepback.service.main;

import com.app.onestepback.domain.dto.main.MainPageDTO;
import com.app.onestepback.repository.MainMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 메인 페이지용 데이터 집계 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final MainMapper mainMapper;

    @Override
    @Transactional(readOnly = true)
    public MainPageDTO.Info getMainPageData() {
        List<MainPageDTO.RisingArtist> risingArtists = mainMapper.selectRisingArtists();
        List<MainPageDTO.FundingSpotlight> fundings = mainMapper.selectFundingSpotlight();
        List<MainPageDTO.LiveFeed> feeds = mainMapper.selectLiveFeeds();

        return MainPageDTO.Info.builder()
                .risingArtists(risingArtists)
                .fundings(fundings)
                .feeds(feeds)
                .build();
    }
}