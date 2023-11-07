package com.app.onestepback.repository;

import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.Pagination;
import com.app.onestepback.mapper.ArtistCommunityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArtistCommunityDAO {
    private final ArtistCommunityMapper artistCommunityMapper;
//    커뮤니티 총 갯수
    public int countCommunity(){
        return artistCommunityMapper.countCommunity();
    }

//    커뮤니티 게시글 리스트
    public List<ArtistPostDTO> selectAllCommunity(Long memberId, Pagination pagination){
        return artistCommunityMapper.selectAllCommunity(memberId, pagination);
    }
}
