package com.app.onestepback.mapper;

import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.Pagination;
import com.app.onestepback.domain.PostFileVO;
import com.app.onestepback.domain.PostTagVO;
import org.apache.ibatis.annotations.Mapper;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ArtistPostMapper {
//    멤버아이디로 조회된 포스트의 갯수
    public int selectCountOfPost(Long memberId);

//    아티스트 홈페이지에서 사용될 작성시간 기준 3개의 포스트
    public List<ArtistPostDTO> select3Posts(Long memberId);

//    포스트 리스트 출력 (페이지네이션)
    public List<ArtistPostDTO> selectAll(Long memberId, Pagination pagination);

//    포스트 작성
    public void insertPost(ArtistPostDTO artistPostDTO);

//    포스트 종류 추가(아티스트 포스트)
    public void insertArtistPost(Long postId);


    public ArtistPostDTO select(Long id);

    public Optional<ArtistPostDTO> selectPrevPost(ArtistPostDTO artistPostDTO);

    public Optional<ArtistPostDTO> selectNextPost(ArtistPostDTO artistPostDTO);

    public void update(ArtistPostDTO artistPostDTO);
}
