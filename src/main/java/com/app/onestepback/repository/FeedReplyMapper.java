package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.feed.reply.FeedReplyDTO;
import com.app.onestepback.domain.model.FeedReplyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FeedReplyMapper {
    void insert(FeedReplyVO feedReplyVO);

    List<FeedReplyDTO> selectAll(Long commentId);

    void update(@Param("id") Long id,
                @Param("memberId") Long memberId,
                @Param("content") String content);

    void softDelete(@Param("id") Long id,
                    @Param("memberId") Long memberId);
}
