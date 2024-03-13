package com.app.onestepback.service.artist;

import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.domain.dto.PostReplyDTO;
import com.app.onestepback.domain.vo.PostReplyVO;

import java.util.List;

public interface PostReplyService {

    public int getReplyCount(Long postId);
    public List<PostReplyDTO> getAllReplies(Long postId, Pagination pagination);

    public void saveReply(PostReplyVO postReplyVO);
}
