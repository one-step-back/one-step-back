package com.app.onestepback.service;

import com.app.onestepback.domain.PostLikeVO;
import com.app.onestepback.repository.PostLikeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeDAO postLikeDAO;
    @Override
    public Optional<PostLikeVO> checkPostLike(PostLikeVO postLikeVO) {
        return postLikeDAO.getPostLikeInfo(postLikeVO);
    }

    @Override
    public void saveLike(PostLikeVO postLikeVO) {
        postLikeDAO.saveLike(postLikeVO);
    }

    @Override
    public void eraseLike(PostLikeVO postLikeVO) {
        postLikeDAO.eraseLike(postLikeVO);
    }
}
