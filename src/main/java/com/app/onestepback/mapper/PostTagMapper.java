package com.app.onestepback.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostTagMapper {
    public List<String> selectAll(Long postId);
}
