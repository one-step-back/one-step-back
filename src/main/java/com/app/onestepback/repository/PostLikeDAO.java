package com.app.onestepback.repository;

import com.app.onestepback.domain.PostLikeVO;
import com.app.onestepback.mapper.PostLikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostLikeDAO {
    private final PostLikeMapper postLikeMapper;

    public Optional<PostLikeVO> getPostLikeInfo(PostLikeVO postLikeVO){
        return postLikeMapper.select(postLikeVO);
    }

    public void saveLike(PostLikeVO postLikeVO){
        postLikeMapper.insert(postLikeVO);
    }

    public void eraseLike(PostLikeVO postLikeVO){
        postLikeMapper.delete(postLikeVO);
    }
}
