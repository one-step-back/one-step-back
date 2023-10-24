package com.app.onestepback.mapper;

import com.app.onestepback.domain.Pagination;
import com.app.onestepback.domain.VideoPostDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoPostMapper {
    public int selectCountOfVideo(Long memberId);

    public List<VideoPostDTO> select3Videos(Long memberId);

    public List<VideoPostDTO> selectAll(Long memberId, Pagination pagination);

    public void insertPost(VideoPostDTO videoPostDTO);

    public void insertVideoPost(VideoPostDTO videoPostDTO);
}
