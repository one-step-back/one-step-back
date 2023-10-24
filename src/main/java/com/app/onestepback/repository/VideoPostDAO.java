package com.app.onestepback.repository;

import com.app.onestepback.domain.Pagination;
import com.app.onestepback.domain.VideoPostDTO;
import com.app.onestepback.mapper.VideoPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VideoPostDAO {
    private final VideoPostMapper videoPostMapper;

    public int getCountOfVideo(Long memberId){return videoPostMapper.selectCountOfVideo(memberId);}

    public List<VideoPostDTO> get3Videos(Long memberId){return videoPostMapper.select3Videos(memberId);}

    public List<VideoPostDTO> getAllVideos(Long memberId, Pagination pagination){return videoPostMapper.selectAll(memberId, pagination);}

    public void savePost(VideoPostDTO videoPostDTO){
        videoPostMapper.insertPost(videoPostDTO);
    }

    public void saveVideoPost(VideoPostDTO videoPostDTO){
        videoPostMapper.insertVideoPost(videoPostDTO);
    }
}
