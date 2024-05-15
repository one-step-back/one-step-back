package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.reply.PostReplyDTO;
import com.app.onestepback.domain.dto.reply.PostReplyWriteDTO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.domain.vo.PostReplyVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostReplyMapper {
    // 포스트id로 해당 포스트 댓글 수 조회
    int selectCountOfReplies(Long postId);

    // 포스트id와 pagination으로 댓글 전체 중 일부 조회
    List<PostReplyDTO> selectAll(Long postId, Pagination pagination);

    void insert(PostReplyWriteDTO postReplyWriteDTO);
}
