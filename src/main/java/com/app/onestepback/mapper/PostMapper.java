package com.app.onestepback.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {
    public void updateViewCount(Long id);

    public void updateToDelete(Long id);
}
