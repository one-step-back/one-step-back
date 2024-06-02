package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.artist.post.ArtistPostDetailDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostEditDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostListDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostRegisterDTO;
import com.app.onestepback.domain.entity.member.Artist;
import com.app.onestepback.domain.type.post.PostSortType;
import com.app.onestepback.domain.vo.Pagination;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ArtistPostMapper {
    //    멤버아이디로 조회된 포스트의 갯수
    int selectCountOfPost(Long artistId);

    List<ArtistPostListDTO> selectAll(PostSortType sortType);

    //    포스트 리스트 출력 (페이지네이션)
    List<ArtistPostListDTO> selectArtistPosts(Long artistId, Long viewerId, Pagination pagination, PostSortType sortType);

    //    포스트 작성
    void insertPost(ArtistPostRegisterDTO artistPostRegisterDTO);

    //    포스트 종류 추가(아티스트 포스트)
    void insertArtistPost(Long postId);

    Optional<ArtistPostDetailDTO> select(Long artistId, Long postId, Long viewerId);

    Optional<ArtistPostEditDTO> selectToEditDTO(Long artistId, Long postId);

    void update(ArtistPostEditDTO artistPostEditDTO);
}
