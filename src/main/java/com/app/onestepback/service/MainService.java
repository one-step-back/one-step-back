package com.app.onestepback.service;

import com.app.onestepback.domain.CombinedPostDTO;

import java.util.List;

public interface MainService {
    public List<CombinedPostDTO> getElementsOfBanners();

    public List<CombinedPostDTO> getPostsForCards();
}
