package com.app.onestepback.mapper;

import com.app.onestepback.domain.VideoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoMapper {
    public int selectCountOfVideo(Long memberId);

    public List<VideoDTO> select3Videos(Long memberId);
}
