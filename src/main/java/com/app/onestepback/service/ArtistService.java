package com.app.onestepback.service;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.VideoDTO;

import java.util.List;
import java.util.Optional;

public interface ArtistService {
    public Optional<ArtistDTO> getArtist(Long id);

    public int getCountOfSubscriber(Long artistId);

    public int getCountOfPost(Long memberId);

    public int getCountOfVideo(Long memberId);

    public List<ArtistPostDTO> get3Posts(Long memberId);

    public List<VideoDTO> get3Videos(Long memberId);

    public List<String> getAllTagsOfPosts(Long artistPostId);

    public List<String> getAllTagsOfVideos(Long videoId);
}
