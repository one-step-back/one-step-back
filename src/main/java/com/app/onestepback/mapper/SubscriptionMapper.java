package com.app.onestepback.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubscriptionMapper {
    public int selectCountOfSubscriber(Long artistId);
}
