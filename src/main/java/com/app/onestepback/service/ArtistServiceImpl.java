package com.app.onestepback.service;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.VideoDTO;
import com.app.onestepback.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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
        return artistPostDAO.get3Posts(memberId);
    }

    @Override
    public List<VideoDTO> get3Videos(Long memberId) {
        return videoDAO.get3Videos(memberId);
    }

    @Override
    public List<String> getAllTags(Long postId) {
        return postTagDAO.getAllTags(postId);
    }

}
