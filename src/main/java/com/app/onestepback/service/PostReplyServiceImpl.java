package com.app.onestepback.service;

import com.app.onestepback.domain.Pagination;
import com.app.onestepback.domain.PostReplyDTO;
import com.app.onestepback.domain.PostReplyVO;
import com.app.onestepback.repository.PostReplyDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PostReplyServiceImpl implements PostReplyService {
    private final PostReplyDAO postReplyDAO;

    @Override
    public int getReplyCount(Long postId) {
        return postReplyDAO.getReplyCount(postId);
    }

    @Override
    public List<PostReplyDTO> getAllReplies(Long postId, Pagination pagination) {
        return postReplyDAO.getAllReplies(postId, pagination);
    }

    @Override
    public void saveReply(PostReplyVO postReplyVO) {
        postReplyDAO.saveReply(postReplyVO);
    }
}
