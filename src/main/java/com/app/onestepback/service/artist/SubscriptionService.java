package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.CombinedPostDTO;
import com.app.onestepback.domain.dto.SubscriptionDTO;

import java.util.List;
import java.util.Optional;

public interface SubscriptionService {
    boolean doSubscribe(Long artistId, Long memberId, boolean status);

    public List<SubscriptionDTO> getAllSubscribedArtist(Long memberId);

    public Optional<CombinedPostDTO> getLatestOne(Long memberId);
}
