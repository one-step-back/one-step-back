package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.postElements.PostFileDTO;
import com.app.onestepback.domain.vo.PostFileVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostFileMapper {
    public void insert(PostFileVO postFileVO);

    void insertAll(List<PostFileVO> postFileVOList);

    List<PostFileDTO> selectAll(Long postId);

    public void delete(Long id);
}
