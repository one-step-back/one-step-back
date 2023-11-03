package com.app.onestepback.mapper;

import com.app.onestepback.domain.Pagination;
import com.app.onestepback.domain.VideoPostDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface VideoPostMapper {
    public int selectCountOfVideo(Long memberId);

    public List<VideoPostDTO> select3Videos(Long memberId);

    public List<VideoPostDTO> selectAll(Long memberId, Pagination pagination);

    public void insertPost(VideoPostDTO videoPostDTO);

    public void insertVideoPost(VideoPostDTO videoPostDTO);

    public VideoPostDTO select(Long id);

    public void update(VideoPostDTO videoPostDTO);

    public void updateVideoLink(VideoPostDTO videoPostDTO);

    public Optional<VideoPostDTO> selectPrevPost(VideoPostDTO videoPostDTO);

    public Optional<VideoPostDTO> selectNextPost(VideoPostDTO videoPostDTO);

    public List<VideoPostDTO> select6Videos();
}
