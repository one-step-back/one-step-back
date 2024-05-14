package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.ArtistDetailDTO;
import com.app.onestepback.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistServiceImpl implements ArtistService {
    private final ArtistDAO artistDAO;


    @Override
    public boolean checkArtistExist(Long artistId) {
        return artistDAO.getArtist(artistId).isPresent();
    }

    //    아티스트 정보 가져오기
    @Override
    public ArtistDetailDTO getArtistDetail(Long artistId, Long viewerId) {
        return artistDAO.getArtistDetail(artistId, viewerId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 아티스트")
        );
    }
}
