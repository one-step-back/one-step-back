package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.video.ArtistVideoListDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoRegisterDTO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.domain.dto.VideoPostDTO;

import java.util.List;
import java.util.Optional;

public interface VideoPostService {

    int getPostCount(Long artistId);

    List<ArtistVideoListDTO> getArtistVideoPage(Long artistId, Long viewerId, Pagination pagination);

    void savePost(ArtistVideoRegisterDTO artistVideoRegisterDTO);

    VideoPostDTO getVideoPost(Long id);

    Optional<VideoPostDTO> getPrevPost(VideoPostDTO videoPostDTO);

    Optional<VideoPostDTO> getNextPost(VideoPostDTO videoPostDTO);

    void editVideoPost(VideoPostDTO videoPostDTO, int numberOfTags);

    void viewCountUp(Long id);

    void erasePost(Long id);

}
