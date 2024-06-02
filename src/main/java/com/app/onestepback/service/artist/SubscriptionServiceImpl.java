package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.SubscriptionDTO;
import com.app.onestepback.repository.SubscriptionDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionDAO subscriptionDAO;

    @Override
    public boolean doSubscribe(Long artistId, Long memberId, boolean status) {
        if (!status) {
            subscriptionDAO.saveSubscription(artistId, memberId);
            return true;
        } else {
            subscriptionDAO.cancelSubscription(artistId, memberId);
            return false;
        }
    }

    @Override
    public List<SubscriptionDTO> getAllSubscribedArtist(Long memberId) {
        return subscriptionDAO.getAllSubscribedArtist(memberId);
    }
}
