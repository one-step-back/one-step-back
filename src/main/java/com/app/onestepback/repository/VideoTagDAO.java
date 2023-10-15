package com.app.onestepback.repository;

import com.app.onestepback.mapper.VideoTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VideoTagDAO {
    private final VideoTagMapper videoTagMapper;

    public List<String> getAllTags(Long videoId){return videoTagMapper.selectAll(videoId);}
}
