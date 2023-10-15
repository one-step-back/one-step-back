package com.app.onestepback.mapper;

import com.app.onestepback.domain.ArtistPostDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArtistPostMapper {
    public int selectCountOfPost(Long memberId);

    public List<ArtistPostDTO> select3Posts(Long memberId);
}
