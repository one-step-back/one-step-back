package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.artist.ArtistPostDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostDetailDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostListDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostRegisterDTO;
import com.app.onestepback.domain.vo.Pagination;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ArtistPostMapper {
    //    멤버아이디로 조회된 포스트의 갯수
    int selectCountOfPost(Long artistId);

    //    포스트 리스트 출력 (페이지네이션)
    public List<ArtistPostListDTO> selectAll(Long artistId, Long viewerId, Pagination pagination);

    //    포스트 작성
    void insertPost(ArtistPostRegisterDTO artistPostRegisterDTO);

    //    포스트 종류 추가(아티스트 포스트)
    void insertArtistPost(Long postId);

//    public ArtistPostDTO select(Long id);

    Optional<ArtistPostDetailDTO> select(Long memberId, Long postId);

    public Optional<ArtistPostDTO> selectPrevPost(ArtistPostDTO artistPostDTO);

    public Optional<ArtistPostDTO> selectNextPost(ArtistPostDTO artistPostDTO);

    public void update(ArtistPostDTO artistPostDTO);
}
