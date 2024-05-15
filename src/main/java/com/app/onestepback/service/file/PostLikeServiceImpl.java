package com.app.onestepback.service.file;

import com.app.onestepback.domain.vo.PostLikeVO;
import com.app.onestepback.repository.PostLikeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeDAO postLikeDAO;

    @Override
    public boolean likePost(Long postId, boolean status, Long memberId) {
        if (!status) {
            postLikeDAO.saveLike(postId, memberId);
            return true;
        } else {
            postLikeDAO.eraseLike(postId, memberId);
            return false;
        }
    }
}
