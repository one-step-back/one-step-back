package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.post.ArtistPostDetailDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostEditDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostListDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostRegisterDTO;
import com.app.onestepback.domain.vo.Pagination;

import java.io.IOException;
import java.util.List;

public interface ArtistPostService {

    int getPostCount(Long memberId);

    List<ArtistPostListDTO> getArtistPostsPage(Long artistId, Long viewerId, Pagination pagination);

    void savePost(ArtistPostRegisterDTO artistPostRegisterDTO) throws IOException;

    ArtistPostDetailDTO getPostDetail(Long artistId, Long postId, Long viewerId);

    ArtistPostEditDTO getEditPost(Long artistId, Long postId);

    void editPost(ArtistPostEditDTO artistPostEditDTO) throws IOException;

    void erasePost(Long postId, Long artistId);
}
