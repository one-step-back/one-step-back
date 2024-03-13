package com.app.onestepback.repository;

import com.app.onestepback.domain.vo.PostFileVO;
import com.app.onestepback.mapper.PostFileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostFileDAO {
    private final PostFileMapper postFileMapper;

    public void saveFile(PostFileVO postFileVO) {
        postFileMapper.insert(postFileVO);
    }

    public List<PostFileVO> getAllFiles(Long postId) {
        return postFileMapper.selectAll(postId);
    }

    public void eraseFile(Long id) {
        postFileMapper.delete(id);
    }
}
