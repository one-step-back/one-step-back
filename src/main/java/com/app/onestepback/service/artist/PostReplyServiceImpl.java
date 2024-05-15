package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.reply.PostReplyDTO;
import com.app.onestepback.domain.dto.reply.PostReplyWriteDTO;
import com.app.onestepback.domain.vo.Pagination;
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
    public void saveReply(PostReplyWriteDTO postReplyWriteDTO) {
        postReplyDAO.saveReply(postReplyWriteDTO);
    }
}
