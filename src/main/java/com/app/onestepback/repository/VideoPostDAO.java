package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.artist.video.ArtistVideoDetailDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoEditDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoListDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoRegisterDTO;
import com.app.onestepback.domain.type.post.PostSortType;
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

    public List<ArtistVideoListDTO> getVideos(PostSortType sortType) {
        return videoPostMapper.selectAll(sortType);
    }

    public List<ArtistVideoListDTO> getArtistVideoPage(Long artistId, Long viewerId, Pagination pagination) {
        return videoPostMapper.selectVideoPosts(artistId, viewerId, pagination);
    }

    public void savePost(ArtistVideoRegisterDTO artistVideoRegisterDTO) {
        videoPostMapper.insertPost(artistVideoRegisterDTO);
    }

    public void saveVideoPost(Long postId, String videoLink) {
        videoPostMapper.insertVideoPost(postId, videoLink);
    }

    public Optional<ArtistVideoDetailDTO> getPost(Long artistId, Long postId, Long viewerId) {
        return videoPostMapper.select(artistId, postId, viewerId);
    }

    public Optional<ArtistVideoEditDTO> getEditPost(Long artistId, Long postId) {
        return  videoPostMapper.selectToEditDTO(artistId, postId);
    }

    public void editVideoPost(ArtistVideoEditDTO artistVideoEditDTO) {
        videoPostMapper.update(artistVideoEditDTO);
    }

    public void editVideoLink(ArtistVideoEditDTO artistVideoEditDTO) {
        videoPostMapper.updateVideoLink(artistVideoEditDTO);
    }

    public List<VideoPostDTO> get6VideoPosts() {
        return videoPostMapper.select6Videos();
    }
}
