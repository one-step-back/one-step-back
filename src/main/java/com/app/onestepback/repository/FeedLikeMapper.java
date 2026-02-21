package com.app.onestepback.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedLikeMapper {
    void insert(long feedId, long memberId);

    void delete(long feedId, long memberId);
}
