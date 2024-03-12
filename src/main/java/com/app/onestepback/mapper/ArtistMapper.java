package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.ArtistDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface ArtistMapper {
//    멤버와 아티스트 단일로 불러옴 -> 아티스트 블로그 정보 출력에 필요.
    public Optional<ArtistDTO> select(Long memberId);

//    로그인 시 해당 클라이언트가 아티스트인지 아닌지 구분하기위한 옵셔널 객체
    public Optional<Long> selectId(Long memberId);
}
