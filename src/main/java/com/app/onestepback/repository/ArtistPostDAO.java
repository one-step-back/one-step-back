package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.artist.ArtistPostDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostDetailDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostListDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostRegisterDTO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.mapper.ArtistPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArtistPostDAO {
    private final ArtistPostMapper artistPostMapper;

    public int getCountOfPost(Long artistId) {
        return artistPostMapper.selectCountOfPost(artistId);
    }

    public List<ArtistPostListDTO> getArtistPostsPage(Long memberId, Long viewerId, Pagination pagination) {
        return artistPostMapper.selectAll(memberId, viewerId, pagination);
    }

    public void savePost(ArtistPostRegisterDTO artistPostRegisterDTO) {
        artistPostMapper.insertPost(artistPostRegisterDTO);
    }

    public void saveArtistPost(Long postId) {
        artistPostMapper.insertArtistPost(postId);
    }

    public Optional<ArtistPostDetailDTO> getPost(Long artistId, Long postId) {
        return artistPostMapper.select(artistId, postId);
    }

    public Optional<ArtistPostDTO> getPrevPost(ArtistPostDTO artistPostDTO) {
        return artistPostMapper.selectPrevPost(artistPostDTO);
    }

    public Optional<ArtistPostDTO> getNextPost(ArtistPostDTO artistPostDTO) {
        return artistPostMapper.selectNextPost(artistPostDTO);
    }

    public void editPost(ArtistPostDTO artistPostDTO) {
        artistPostMapper.update(artistPostDTO);
    }
}
