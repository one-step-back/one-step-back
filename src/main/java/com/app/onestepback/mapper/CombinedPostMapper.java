package com.app.onestepback.mapper;

import com.app.onestepback.domain.CombinedPostDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CombinedPostMapper {
    public List<CombinedPostDTO> select5Posts();

    public List<CombinedPostDTO> select4Posts();
}
