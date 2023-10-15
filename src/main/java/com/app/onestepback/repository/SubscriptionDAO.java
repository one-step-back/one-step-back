package com.app.onestepback.repository;

import com.app.onestepback.mapper.SubscriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SubscriptionDAO {
    private final SubscriptionMapper subscriptionMapper;

    public int getCountOfSubscriber(Long artistId) {
        return subscriptionMapper.selectCountOfSubscriber(artistId);
    }
}
