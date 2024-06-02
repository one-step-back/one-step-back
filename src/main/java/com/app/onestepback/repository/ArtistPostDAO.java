package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.artist.post.ArtistPostDetailDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostEditDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostListDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostRegisterDTO;
import com.app.onestepback.domain.type.post.PostSortType;
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

    public List<ArtistPostListDTO> getArtistPosts(PostSortType sortType) {
        return artistPostMapper.selectAll(sortType);
    }

    public List<ArtistPostListDTO> getArtistPostsPage(Long memberId, Long viewerId, Pagination pagination, PostSortType sortType) {
        return artistPostMapper.selectArtistPosts(memberId, viewerId, pagination, sortType);
    }

    public void savePost(ArtistPostRegisterDTO artistPostRegisterDTO) {
        artistPostMapper.insertPost(artistPostRegisterDTO);
    }

    public void saveArtistPost(Long postId) {
        artistPostMapper.insertArtistPost(postId);
    }

    public Optional<ArtistPostDetailDTO> getPost(Long artistId, Long postId, Long viewerId) {
        return artistPostMapper.select(artistId, postId, viewerId);
    }

    public Optional<ArtistPostEditDTO> getEditPost(Long artistId, Long postId) {
        return artistPostMapper.selectToEditDTO(artistId, postId);
    }

    public void editPost(ArtistPostEditDTO artistPostEditDTO) {
        artistPostMapper.update(artistPostEditDTO);
    }
}
