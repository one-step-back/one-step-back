package com.app.onestepback.service;

import com.app.onestepback.domain.Pagination;
import com.app.onestepback.domain.PostReplyDTO;
import com.app.onestepback.domain.PostReplyVO;

import java.util.List;

public interface PostReplyService {

    public int getReplyCount(Long postId);
    public List<PostReplyDTO> getAllReplies(Long postId, Pagination pagination);

    public void saveReply(PostReplyVO postReplyVO);
}
