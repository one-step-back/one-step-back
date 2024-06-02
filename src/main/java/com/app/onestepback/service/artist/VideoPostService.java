package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.video.ArtistVideoDetailDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoEditDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoListDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoRegisterDTO;
import com.app.onestepback.domain.type.post.PostSortType;
import com.app.onestepback.domain.vo.Pagination;

import java.util.List;

public interface VideoPostService {

    int getPostCount(Long artistId);

    List<ArtistVideoListDTO> getVideos(PostSortType sortType);

    List<ArtistVideoListDTO> getArtistVideoPage(Long artistId, Long viewerId, Pagination pagination);

    void savePost(ArtistVideoRegisterDTO artistVideoRegisterDTO);

    ArtistVideoDetailDTO getPostDetail(Long artistId, Long postId, Long viewerId);

    ArtistVideoEditDTO getEditPost(Long artistId, Long postId);

    void editVideoPost(ArtistVideoEditDTO artistVideoEditDTO);

    void viewCountUp(Long id);

    void erasePost(Long artistId, Long postId);

}
