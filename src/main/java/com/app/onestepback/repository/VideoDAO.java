package com.app.onestepback.repository;

import com.app.onestepback.domain.VideoDTO;
import com.app.onestepback.mapper.VideoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VideoDAO {
    private final VideoMapper videoMapper;

    public int getCountOfVideo(Long memberId){return videoMapper.selectCountOfVideo(memberId);}

    public List<VideoDTO> get3Videos(Long memberId){return videoMapper.select3Videos(memberId);}
}
