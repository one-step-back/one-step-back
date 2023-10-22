package com.app.onestepback.mapper;

import com.app.onestepback.domain.ArtistDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface ArtistMapper {
//    멤버와 아티스트 단일로 불러옴 -> 아티스트 블로그 정보 출력에 필요.
    public Optional<ArtistDTO> select(Long memberId);
}
