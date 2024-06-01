package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.SubscriptionDTO;
import com.app.onestepback.mapper.SubscriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubscriptionDAO {
    private final SubscriptionMapper subscriptionMapper;

    public int getCountOfSubscriber(Long artistId) {
        return subscriptionMapper.selectCountOfSubscriber(artistId);
    }

    public void saveSubscription(Long artistId, Long memberId) {
        subscriptionMapper.insert(artistId, memberId);
    }

    public void cancelSubscription(Long artistId, Long memberId) {
        subscriptionMapper.delete(artistId, memberId);
    }

    public List<SubscriptionDTO> getAllSubscribedArtist(Long memberId) {
        return subscriptionMapper.selectAll(memberId);
    }
}
