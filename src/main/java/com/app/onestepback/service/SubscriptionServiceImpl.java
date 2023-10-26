package com.app.onestepback.service;

import com.app.onestepback.domain.CombinedPostDTO;
import com.app.onestepback.domain.SubscriptionDTO;
import com.app.onestepback.domain.SubscriptionVO;
import com.app.onestepback.repository.CombinedPostDAO;
import com.app.onestepback.repository.SubscriptionDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionDAO subscriptionDAO;
    private final CombinedPostDAO combinedPostDAO;

    @Override
    public Optional<SubscriptionVO> checkSubscription(SubscriptionVO subscriptionVO) {
        return subscriptionDAO.checkSubscription(subscriptionVO);
    }

    @Override
    public void saveSubscription(SubscriptionVO subscriptionVO) {
        subscriptionDAO.saveSubscription(subscriptionVO);
    }

    @Override
    public void cancelSubscription(SubscriptionVO subscriptionVO) {
        subscriptionDAO.cancelSubscription(subscriptionVO);
    }

    @Override
    public List<SubscriptionDTO> getAllSubscribedArtist(Long memberId) {
        return subscriptionDAO.getAllSubscribedArtist(memberId);
    }

    @Override
    public Optional<CombinedPostDTO> getLatestOne(Long memberId) {
        return combinedPostDAO.getLatestOne(memberId);
    }
}
