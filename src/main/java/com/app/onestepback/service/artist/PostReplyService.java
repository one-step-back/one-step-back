package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.reply.PostReplyDTO;
import com.app.onestepback.domain.dto.reply.PostReplyWriteDTO;
import com.app.onestepback.domain.vo.Pagination;

import java.util.List;

public interface PostReplyService {

    int getReplyCount(Long postId);

    List<PostReplyDTO> getAllReplies(Long postId, Pagination pagination);

    void saveReply(PostReplyWriteDTO postReplyWriteDTO);
}
