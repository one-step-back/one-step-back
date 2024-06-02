package com.app.onestepback.service;

import com.app.onestepback.domain.dto.CrowdFundingDTO;
import com.app.onestepback.domain.dto.VideoPostDTO;
import com.app.onestepback.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final PostTagDAO postTagDAO;
    private final VideoPostDAO videoPostDAO;
    private final ArtistCrowdFundingDAO artistCrowdFundingDAO;

//    @Override
//    public List<CombinedPostDTO> getElementsOfBanners() {
//        List<CombinedPostDTO> posts = combinedPostDAO.get5Posts();
//
//        return posts;
//    }
//
//
//    @Override
//    public List<CombinedPostDTO> getPostsForCards() {
//        return combinedPostDAO.get4Posts();
//    }
//
//    @Override
//    public List<VideoPostDTO> getVideosForCards() {
//        return videoPostDAO.get6VideoPosts();
//    }
//
//    @Override
//    public List<CrowdFundingDTO> getCrowdfundingsForCards() {
//        return artistCrowdFundingDAO.get4FundingsRandomly();
//    }

}
