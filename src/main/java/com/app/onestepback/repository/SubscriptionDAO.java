package com.app.onestepback.repository;

import com.app.onestepback.domain.SubscriptionDTO;
import com.app.onestepback.domain.SubscriptionVO;
import com.app.onestepback.mapper.SubscriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SubscriptionDAO {
    private final SubscriptionMapper subscriptionMapper;

    public int getCountOfSubscriber(Long artistId) {
        return subscriptionMapper.selectCountOfSubscriber(artistId);
    }

    public Optional<SubscriptionVO> checkSubscription(SubscriptionVO subscriptionVO){return subscriptionMapper.select(subscriptionVO);}

    public void saveSubscription(SubscriptionVO subscriptionVO){
        subscriptionMapper.insert(subscriptionVO);
    }

    public void cancelSubscription(SubscriptionVO subscriptionVO){
        subscriptionMapper.delete(subscriptionVO);
    }

    public List<SubscriptionDTO> getAllSubscribedArtist(Long memberId){
        return subscriptionMapper.selectAll(memberId);
    }
}
