package com.app.onestepback.repository;

import com.app.onestepback.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostDAO {
    private final PostMapper postMapper;

    public void viewCountUp(Long id){
        postMapper.updateViewCount(id);
    }

    public void erasePost(Long postId, Long artistId){
        postMapper.softDelete(postId, artistId);
    }
}
