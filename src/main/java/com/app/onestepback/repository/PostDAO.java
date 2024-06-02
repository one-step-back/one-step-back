package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.PostDTO;
import com.app.onestepback.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostDAO {
    private final PostMapper postMapper;

    public void viewCountUp(Long id) {
        postMapper.updateViewCount(id);
    }

    public List<PostDTO> getPosts(int limit) {
        return postMapper.selectPosts(limit);
    }

    public void erasePost(Long postId, Long artistId) {
        postMapper.softDelete(postId, artistId);
    }
}
