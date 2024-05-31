package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.artist.video.ArtistVideoListDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoRegisterDTO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.domain.dto.VideoPostDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface VideoPostMapper {
    int selectCountOfVideo(Long artistId);

    List<ArtistVideoListDTO> selectAll(Long artistId, Long viewerId, Pagination pagination);

    void insertPost(ArtistVideoRegisterDTO artistVideoRegisterDTO);

    void insertVideoPost(Long postId, String videoLink);

    VideoPostDTO select(Long id);

    void update(VideoPostDTO videoPostDTO);

    void updateVideoLink(VideoPostDTO videoPostDTO);

    Optional<VideoPostDTO> selectPrevPost(VideoPostDTO videoPostDTO);

    Optional<VideoPostDTO> selectNextPost(VideoPostDTO videoPostDTO);

    List<VideoPostDTO> select6Videos();
}
