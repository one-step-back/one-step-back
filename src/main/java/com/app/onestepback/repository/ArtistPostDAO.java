package com.app.onestepback.repository;

import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.Pagination;
import com.app.onestepback.mapper.ArtistPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArtistPostDAO {
    private final ArtistPostMapper artistPostMapper;

    public int getCountOfPost(Long memberId){return artistPostMapper.selectCountOfPost(memberId);}

    public List<ArtistPostDTO> get3Posts(Long memberId){return artistPostMapper.select3Posts(memberId);}

    public List<ArtistPostDTO> getAllPosts(Long memberId, Pagination pagination){return artistPostMapper.selectAll(memberId, pagination);}
}
