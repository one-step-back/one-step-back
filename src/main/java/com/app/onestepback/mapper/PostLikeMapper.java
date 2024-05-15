package com.app.onestepback.mapper;

import com.app.onestepback.domain.vo.PostLikeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface PostLikeMapper {
    Optional<PostLikeVO> select(PostLikeVO postLikeVO);

    void insert(Long postId, Long memberId);

    void delete(Long postId, Long memberId);
}
