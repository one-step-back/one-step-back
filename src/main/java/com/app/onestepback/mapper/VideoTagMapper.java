package com.app.onestepback.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoTagMapper {
    public List<String> selectAll(Long videoId);
}
