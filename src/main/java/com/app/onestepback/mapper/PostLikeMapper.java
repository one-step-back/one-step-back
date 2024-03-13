package com.app.onestepback.mapper;

import com.app.onestepback.domain.vo.PostLikeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface PostLikeMapper {
    public Optional<PostLikeVO> select(PostLikeVO postLikeVO);

    public void insert(PostLikeVO postLikeVO);

    public void delete(PostLikeVO postLikeVO);
}
