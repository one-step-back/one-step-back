package com.app.onestepback.service.file;

public interface PostLikeService {
    boolean likePost(Long postId, boolean status, Long memberId);
}
