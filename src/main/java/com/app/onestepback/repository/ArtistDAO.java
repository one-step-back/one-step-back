package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.artist.ArtistDTO;
import com.app.onestepback.mapper.ArtistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArtistDAO {
    private final ArtistMapper artistMapper;

//    아티스트 불러옴 -> 블로그 출력
    public Optional<ArtistDTO> getArtist(Long memberId){
        return artistMapper.select(memberId);
    }

    public Optional<Long> checkArtist(Long memberId){return artistMapper.selectId(memberId);}
}
