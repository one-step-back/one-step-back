package com.app.onestepback.repository;

import com.app.onestepback.mapper.ArtistPostTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArtistPostTagDAO {
    private final ArtistPostTagMapper artistPostTagMapper;

    public List<String> getAllTags(Long artistPostId){return artistPostTagMapper.selectAll(artistPostId);}
}
