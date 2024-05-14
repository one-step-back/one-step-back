package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.ArtistDetailDTO;

public interface ArtistService {
    boolean checkArtistExist(Long artistId);

    ArtistDetailDTO getArtistDetail(Long artistId, Long viewerId);
}
