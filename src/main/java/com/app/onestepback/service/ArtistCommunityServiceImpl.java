package com.app.onestepback.service;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.Pagination;
import com.app.onestepback.repository.ArtistCommunityDAO;
import com.app.onestepback.repository.ArtistPostDAO;
import com.app.onestepback.repository.PostTagDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArtistCommunityServiceImpl implements ArtistCommunityService {
    private final ArtistCommunityDAO artistCommunityDAO;
    private final PostTagDAO postTagDAO;

    @Override
    public int getCommunityCount() {
        return artistCommunityDAO.countCommunity();
    }

    @Override
    public List<ArtistPostDTO> getselectAllCommunity(Long memberId, Pagination pagination) {
        List<ArtistPostDTO> posts = artistCommunityDAO.selectAllCommunity(memberId, pagination);

        for (ArtistPostDTO post : posts) {
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
        return artistCommunityDAO.selectAllCommunity(memberId, pagination);
    }

}

