package com.app.onestepback.service;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.Pagination;

import java.util.List;

public interface ArtistCommunityService {
    //    커뮤니티 총 갯수
    public int getCommunityCount();

    //    커뮤니티 게시글 리스트
    public List<ArtistPostDTO> getselectAllCommunity(Long memberId, Pagination pagination);
}
