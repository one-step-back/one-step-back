package com.app.onestepback.service;

import com.app.onestepback.domain.dto.CombinedPostDTO;
import com.app.onestepback.domain.dto.CrowdFundingDTO;
import com.app.onestepback.domain.dto.VideoPostDTO;

import java.util.List;

public interface MainService {
    public List<CombinedPostDTO> getElementsOfBanners();

    public List<CombinedPostDTO> getPostsForCards();

    public List<VideoPostDTO> getVideosForCards();

    public List<CrowdFundingDTO> getCrowdfundingsForCards();

}
