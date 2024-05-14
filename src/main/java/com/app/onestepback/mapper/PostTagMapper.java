package com.app.onestepback.mapper;

import com.app.onestepback.domain.vo.PostTagVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostTagMapper {
    List<String> selectAll(Long postId);

    void insert(PostTagVO postTagVO);

    void insertAll(List<PostTagVO> postTagVOList);

    void delete(Long postId);

    void deleteAll(List<PostTagVO> postTagVOList);
}
