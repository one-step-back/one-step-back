package com.app.onestepback.mapper;

import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.Pagination;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArtistCommunityMapper {
//    커뮤니티 총 갯수
    public int countCommunity();


//    커뮤니티 게시글 리스트
    public List<ArtistPostDTO> selectAllCommunity(Long memberId, Pagination pagination);

}
