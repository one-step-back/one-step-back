package com.app.onestepback.mapper;

import com.app.onestepback.domain.vo.PostFileVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostFileMapper {
    public void insert(PostFileVO postFileVO);

    public List<PostFileVO> selectAll(Long postId);

    public void delete(Long id);
}
