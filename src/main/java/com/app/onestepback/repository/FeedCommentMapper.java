package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.feed.comment.FeedCommentDTO;
import com.app.onestepback.domain.model.FeedCommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FeedCommentMapper {
    void insert(FeedCommentVO feedCommentVO);

    List<FeedCommentDTO> selectSlice(@Param("feedId") Long feedId,
                                     @Param("lastCommentId") Long lastCommentId,
                                     @Param("limit") int limit);



    void update(
            @Param("id") Long id,
            @Param("memberId") Long memberId,
            @Param("content") String content
    );

    void softDelete(@Param("id") Long id, @Param("memberId") Long memberId);
}