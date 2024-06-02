package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.PostDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    void updateViewCount(Long id);

    List<PostDTO> selectPosts(int limit);

    void softDelete(Long postId, Long artistId);
}
