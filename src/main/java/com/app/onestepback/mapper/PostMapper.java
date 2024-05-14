package com.app.onestepback.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {
    void updateViewCount(Long id);

    void softDelete(Long postId, Long artistId);
}
