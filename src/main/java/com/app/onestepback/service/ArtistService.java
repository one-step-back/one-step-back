package com.app.onestepback.service;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.SubscriptionVO;
import com.app.onestepback.domain.VideoDTO;

import java.util.List;
import java.util.Optional;

public interface ArtistService {
    public Optional<ArtistDTO> getArtist(Long memberId);

    public List<ArtistPostDTO> get3Posts(Long memberId);

    public List<VideoDTO> get3Videos(Long memberId);
}
