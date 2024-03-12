package com.app.onestepback.mapper;

import com.app.onestepback.domain.vo.PostTagVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostTagMapper {
    public List<String> selectAll(Long postId);

    public void insert(PostTagVO postTagVO);

    public void delete(Long postId);
}
