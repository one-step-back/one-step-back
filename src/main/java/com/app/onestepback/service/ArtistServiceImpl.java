package com.app.onestepback.service;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.repository.ArtistDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    private final ArtistDAO artistDAO;

//    아티스트 정보 가져오기
    @Override
    public Optional<ArtistDTO> getArtist(Long id) {
        return artistDAO.getArtist(id);
    }
}
