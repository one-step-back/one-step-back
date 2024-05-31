package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.artist.video.ArtistVideoListDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoRegisterDTO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.domain.dto.VideoPostDTO;
import com.app.onestepback.mapper.VideoPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VideoPostDAO {
    private final VideoPostMapper videoPostMapper;

    public int getCountOfVideo(Long artistId) {
        return videoPostMapper.selectCountOfVideo(artistId);
    }

    public List<ArtistVideoListDTO> getArtistVideoPage(Long artistId, Long viewerId, Pagination pagination) {
        return videoPostMapper.selectAll(artistId, viewerId, pagination);
    }

    public void savePost(ArtistVideoRegisterDTO artistVideoRegisterDTO) {
        videoPostMapper.insertPost(artistVideoRegisterDTO);
    }

    public void saveVideoPost(Long postId, String videoLink) {
        videoPostMapper.insertVideoPost(postId, videoLink);
    }

    public VideoPostDTO getVideoPost(Long id) {
        return videoPostMapper.select(id);
    }

    public Optional<VideoPostDTO> getPrevPost(VideoPostDTO videoPostDTO) {
        return videoPostMapper.selectPrevPost(videoPostDTO);
    }

    public Optional<VideoPostDTO> getNextPost(VideoPostDTO videoPostDTO) {
        return videoPostMapper.selectNextPost(videoPostDTO);
    }

    public void editVideoPost(VideoPostDTO videoPostDTO) {
        videoPostMapper.update(videoPostDTO);
    }

    public void editVideoLink(VideoPostDTO videoPostDTO) {
        videoPostMapper.updateVideoLink(videoPostDTO);
    }

    public List<VideoPostDTO> get6VideoPosts() {
        return videoPostMapper.select6Videos();
    }
}
