package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.ArtistDetailDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostDTO;
import com.app.onestepback.domain.dto.VideoPostDTO;
import com.app.onestepback.domain.vo.ArtistVO;

import java.util.List;

public interface ArtistService {
    boolean checkArtistExist(Long artistId);

    ArtistDetailDTO getArtistDetail(Long artistId);
}
