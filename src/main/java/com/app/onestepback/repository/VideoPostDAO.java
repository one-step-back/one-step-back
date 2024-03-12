package com.app.onestepback.repository;

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

    public int getCountOfVideo(Long memberId){return videoPostMapper.selectCountOfVideo(memberId);}

    public List<VideoPostDTO> get3Videos(Long memberId){return videoPostMapper.select3Videos(memberId);}

    public List<VideoPostDTO> getAllVideos(Long memberId, Pagination pagination){return videoPostMapper.selectAll(memberId, pagination);}

    public void savePost(VideoPostDTO videoPostDTO){
        videoPostMapper.insertPost(videoPostDTO);
    }

    public void saveVideoPost(VideoPostDTO videoPostDTO){
        videoPostMapper.insertVideoPost(videoPostDTO);
    }

    public VideoPostDTO getVideoPost(Long id){
        return videoPostMapper.select(id);
    }

    public Optional<VideoPostDTO> getPrevPost(VideoPostDTO videoPostDTO){return videoPostMapper.selectPrevPost(videoPostDTO);}

    public Optional<VideoPostDTO> getNextPost(VideoPostDTO videoPostDTO){return videoPostMapper.selectNextPost(videoPostDTO);}

    public void editVideoPost(VideoPostDTO videoPostDTO){
        videoPostMapper.update(videoPostDTO);
    }

    public void editVideoLink(VideoPostDTO videoPostDTO){
        videoPostMapper.updateVideoLink(videoPostDTO);
    }

    public List<VideoPostDTO> get6VideoPosts(){
        return videoPostMapper.select6Videos();
    }
}
