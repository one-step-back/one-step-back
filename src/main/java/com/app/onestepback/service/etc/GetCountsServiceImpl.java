package com.app.onestepback.service.etc;

import com.app.onestepback.repository.ArtistPostDAO;
import com.app.onestepback.repository.SubscriptionDAO;
import com.app.onestepback.repository.VideoPostDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCountsServiceImpl implements GetCountsService {
    private final SubscriptionDAO subscriptionDAO;
    private final ArtistPostDAO artistPostDAO;
    private final VideoPostDAO videoPostDAO;

    @Override
    public int getCountOfSubscriber(Long artistId) {
        return subscriptionDAO.getCountOfSubscriber(artistId);
    }

    @Override
    public int getCountOfPost(Long memberId) {
        return artistPostDAO.getCountOfPost(memberId);
    }

    @Override
    public int getCountOfVideo(Long memberId) {
        return videoPostDAO.getCountOfVideo(memberId);
    }
}
