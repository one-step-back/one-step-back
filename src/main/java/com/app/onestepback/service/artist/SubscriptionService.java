package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.CombinedPostDTO;
import com.app.onestepback.domain.dto.SubscriptionDTO;
import com.app.onestepback.domain.vo.SubscriptionVO;

import java.util.List;
import java.util.Optional;

public interface SubscriptionService {
    public Optional<SubscriptionVO> checkSubscription(SubscriptionVO subscriptionVO);

    public void saveSubscription(SubscriptionVO subscriptionVO);

    public void cancelSubscription(SubscriptionVO subscriptionVO);

    public List<SubscriptionDTO> getAllSubscribedArtist(Long memberId);

    public Optional<CombinedPostDTO> getLatestOne(Long memberId);
}
