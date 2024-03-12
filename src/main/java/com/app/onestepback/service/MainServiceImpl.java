package com.app.onestepback.service;

import com.app.onestepback.domain.dto.CombinedPostDTO;
import com.app.onestepback.domain.dto.CrowdFundingDTO;
import com.app.onestepback.domain.dto.VideoPostDTO;
import com.app.onestepback.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final CombinedPostDAO combinedPostDAO;
    private final PostTagDAO postTagDAO;
    private final VideoPostDAO videoPostDAO;
    private final ArtistCrowdFundingDAO artistCrowdFundingDAO;

    @Override
    public List<CombinedPostDTO> getElementsOfBanners() {
        List<CombinedPostDTO> posts = combinedPostDAO.get5Posts();

        for (CombinedPostDTO post : posts) {
            List<String> tags = postTagDAO.getAllTags(post.getId());

            if (!tags.isEmpty()) {
                post.setTag1(tags.get(0));
            }
            if (tags.size() >= 2) {
                post.setTag2(tags.get(1));
            }
            if (tags.size() >= 3) {
                post.setTag3(tags.get(2));
            }
            if (tags.size() >= 4) {
                post.setTag4(tags.get(3));
            }
            if (tags.size() >= 5) {
                post.setTag5(tags.get(4));
            }
        }
        return posts;
    }


    @Override
    public List<CombinedPostDTO> getPostsForCards() {
        return combinedPostDAO.get4Posts();
    }

    @Override
    public List<VideoPostDTO> getVideosForCards() {
        return videoPostDAO.get6VideoPosts();
    }

    @Override
    public List<CrowdFundingDTO> getCrowdfundingsForCards() {
        return artistCrowdFundingDAO.get4FundingsRandomly();
    }

}
