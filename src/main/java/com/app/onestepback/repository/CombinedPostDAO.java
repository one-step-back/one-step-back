package com.app.onestepback.repository;

import com.app.onestepback.domain.CombinedPostDTO;
import com.app.onestepback.mapper.CombinedPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CombinedPostDAO {
    private final CombinedPostMapper combinedPostMapper;

    public List<CombinedPostDTO> get5Posts(){
        return combinedPostMapper.select5Posts();
    }

    public List<CombinedPostDTO> get4Posts(){
        return combinedPostMapper.select4Posts();
    }

    public Optional<CombinedPostDTO> getLatestOne(Long memberId){
        return combinedPostMapper.selectLatestOne(memberId);
    }
}
