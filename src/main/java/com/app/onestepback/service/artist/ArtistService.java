package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.ArtistDTO;
import com.app.onestepback.domain.dto.ArtistPostDTO;
import com.app.onestepback.domain.dto.VideoPostDTO;

import java.util.List;
import java.util.Optional;

public interface ArtistService {
    public Optional<ArtistDTO> getArtist(Long memberId);

    public List<ArtistPostDTO> get3Posts(Long memberId);

    public List<VideoPostDTO> get3Videos(Long memberId);
}
