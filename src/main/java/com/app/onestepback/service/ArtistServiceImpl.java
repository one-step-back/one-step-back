package com.app.onestepback.service;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.SubscriptionVO;
import com.app.onestepback.domain.VideoDTO;
import com.app.onestepback.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistServiceImpl implements ArtistService {
    private final ArtistDAO artistDAO;
    private final SubscriptionDAO subscriptionDAO;
    private final ArtistPostDAO artistPostDAO;
    private final VideoDAO videoDAO;
    private final PostTagDAO postTagDAO;

//    아티스트 정보 가져오기
    @Override
    public Optional<ArtistDTO> getArtist(Long id) {
        return artistDAO.getArtist(id);
    }

//    구독자 수 정보 가져오기
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
        return videoDAO.getCountOfVideo(memberId);
    }

    @Override
    public List<ArtistPostDTO> get3Posts(Long memberId) {
        List<ArtistPostDTO> posts = artistPostDAO.get3Posts(memberId);

        for (ArtistPostDTO post : posts){
            List<String> tags = postTagDAO.getAllTags(post.getId());

            if (!tags.isEmpty()) {
                post.setTag1(tags.get(0));
            }
            if (tags.size() >= 2) {
                post.setTag2(tags.get(1));
            }
            if (tags.size() >= 3) {
                post.setTag3(tags.get(2));
            }
            if (tags.size() >= 4) {
                post.setTag4(tags.get(3));
            }
            if (tags.size() >= 5) {
                post.setTag5(tags.get(4));
            }
        }
        return posts;
    }

    @Override
    public List<VideoDTO> get3Videos(Long memberId) {
        List<VideoDTO> videos = videoDAO.get3Videos(memberId);

        for (VideoDTO video : videos){
            List<String> tags = postTagDAO.getAllTags(video.getId());

            if (!tags.isEmpty()) {
                video.setTag1(tags.get(0));
            }
            if (tags.size() >= 2) {
                video.setTag2(tags.get(1));
            }
            if (tags.size() >= 3) {
                video.setTag3(tags.get(2));
            }
            if (tags.size() >= 4) {
                video.setTag4(tags.get(3));
            }
            if (tags.size() >= 5) {
                video.setTag5(tags.get(4));
            }
        }
        return videos;
    }

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

}
