package com.app.onestepback.repository;

import com.app.onestepback.domain.vo.PostLikeVO;
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

    public void saveLike(Long postId, Long memberId){
        postLikeMapper.insert(postId, memberId);
    }

    public void eraseLike(Long postId, Long memberId){
        postLikeMapper.delete(postId, memberId);
    }
}
