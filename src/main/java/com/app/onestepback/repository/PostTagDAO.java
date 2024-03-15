package com.app.onestepback.repository;

import com.app.onestepback.domain.vo.PostTagVO;
import com.app.onestepback.mapper.PostTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostTagDAO {
    private final PostTagMapper postTagMapper;

    public List<String> getAllTags(Long postId){return postTagMapper.selectAll(postId);}

    public void savePostTag(PostTagVO postTagVO){postTagMapper.insert(postTagVO);}

    public void saveAllTags(List<PostTagVO> postTagVOList) {
        postTagMapper.insertAll(postTagVOList);
    }

    public void deletePostTag(Long postId){postTagMapper.delete(postId);}
}
