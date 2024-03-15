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
    public int selectCountOfPost(Long memberId);

    //    아티스트 홈페이지에서 사용될 작성시간 기준 3개의 포스트
    public List<ArtistPostDTO> select3Posts(Long memberId);

    //    포스트 리스트 출력 (페이지네이션)
    public List<ArtistPostListDTO> selectAll(Long memberId, Pagination pagination);

    //    포스트 작성
    public void insertPost(ArtistPostRegisterDTO artistPostRegisterDTO);

    //    포스트 종류 추가(아티스트 포스트)
    public void insertArtistPost(Long postId);

//    public ArtistPostDTO select(Long id);

    Optional<ArtistPostDetailDTO> select(Long memberId, Long postId);

    public Optional<ArtistPostDTO> selectPrevPost(ArtistPostDTO artistPostDTO);

    public Optional<ArtistPostDTO> selectNextPost(ArtistPostDTO artistPostDTO);

    public void update(ArtistPostDTO artistPostDTO);
}
