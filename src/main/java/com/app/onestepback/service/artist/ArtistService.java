package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.ArtistDetailDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostDTO;
import com.app.onestepback.domain.dto.VideoPostDTO;

import java.util.List;

public interface ArtistService {
    ArtistDetailDTO getArtist(Long artistId);

    public List<ArtistPostDTO> get3Posts(Long memberId);

    public List<VideoPostDTO> get3Videos(Long memberId);
}
