package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.SubscriptionDTO;

import java.util.List;
import java.util.Optional;

public interface SubscriptionService {
    boolean doSubscribe(Long artistId, Long memberId, boolean status);

     List<SubscriptionDTO> getAllSubscribedArtist(Long memberId);
}
