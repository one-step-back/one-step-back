package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.artist.ArtistDetailDTO;
import com.app.onestepback.domain.vo.ArtistVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface ArtistMapper {

    Optional<ArtistVO> selectById(Long artistId);

//    멤버와 아티스트 단일로 불러옴 -> 아티스트 블로그 정보 출력에 필요.
    Optional<ArtistDetailDTO> selectDetail(Long artistId);

//    로그인 시 해당 클라이언트가 아티스트인지 아닌지 구분하기위한 옵셔널 객체
    Optional<Long> selectId(Long memberId);
}
