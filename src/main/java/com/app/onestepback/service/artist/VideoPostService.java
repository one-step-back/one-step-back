package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.post.ArtistPostDetailDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoDetailDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoListDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoRegisterDTO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.domain.dto.VideoPostDTO;

import java.util.List;

public interface VideoPostService {

    int getPostCount(Long artistId);

    List<ArtistVideoListDTO> getArtistVideoPage(Long artistId, Long viewerId, Pagination pagination);

    void savePost(ArtistVideoRegisterDTO artistVideoRegisterDTO);

    ArtistVideoDetailDTO getPostDetail(Long artistId, Long postId, Long viewerId);

    void editVideoPost(VideoPostDTO videoPostDTO, int numberOfTags);

    void viewCountUp(Long id);

    void erasePost(Long id);

}
