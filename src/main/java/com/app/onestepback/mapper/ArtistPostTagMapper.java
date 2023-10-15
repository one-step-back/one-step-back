package com.app.onestepback.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArtistPostTagMapper {
    public List<String> selectAll(Long artistPostId);
}
