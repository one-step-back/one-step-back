package com.app.onestepback.service;

import com.app.onestepback.domain.SubscriptionVO;

import java.util.Optional;

public interface SubscriptionService {
    public Optional<SubscriptionVO> checkSubscription(SubscriptionVO subscriptionVO);

    public void saveSubscription(SubscriptionVO subscriptionVO);

    public void cancelSubscription(SubscriptionVO subscriptionVO);
}
