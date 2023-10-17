package com.app.onestepback.service;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.Pagination;
import com.app.onestepback.repository.ArtistDAO;
import com.app.onestepback.repository.ArtistPostDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtistPostServiceImpl implements ArtistPostService {
    private final ArtistDAO artistDAO;
    private final ArtistPostDAO artistPostDAO;

    @Override
    public Optional<ArtistDTO> getArtist(Long id) {
        return artistDAO.getArtist(id);
    }

    @Override
    public int getPostCount(Long memberId) {
        return artistPostDAO.getCountOfPost(memberId);
    }

    @Override
    public List<ArtistPostDTO> getAllPosts(Long memberId, Pagination pagination) {
        return artistPostDAO.getAllPosts(memberId, pagination);
    }
}
