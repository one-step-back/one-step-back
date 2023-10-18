package com.app.onestepback.service;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.Pagination;
import com.app.onestepback.repository.ArtistDAO;
import com.app.onestepback.repository.ArtistPostDAO;
import com.app.onestepback.repository.PostTagDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtistPostServiceImpl implements ArtistPostService {
    private final ArtistDAO artistDAO;
    private final ArtistPostDAO artistPostDAO;
    private final PostTagDAO postTagDAO;

    @Override
    public Optional<ArtistDTO> getArtist(Long id) {
        return artistDAO.getArtist(id);
    }

    @Override
    public int getPostCount(Long memberId) {
        return artistPostDAO.getCountOfPost(memberId);
    }

    @Override
    public List<ArtistPostDTO> getAllPosts(Long memberId, Pagination pagination) {
        List<ArtistPostDTO> posts = artistPostDAO.getAllPosts(memberId, pagination);

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
        return posts;
    }

    @Override
    public List<String> getAllTags(Long postId) {
        return postTagDAO.getAllTags(postId);
    }
}
