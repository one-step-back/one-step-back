package com.app.onestepback.service;

import com.app.onestepback.domain.ArtistDTO;

import java.util.Optional;

public interface ArtistService {
    public Optional<ArtistDTO> getArtist(Long id);
}
