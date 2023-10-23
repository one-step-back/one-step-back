package com.app.onestepback.mapper;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.domain.CrowdFundingDTO;
import com.app.onestepback.domain.Pagination;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ArtistCrowdFundingMapper {
    //    멤버아이디로 조회된 크라우드 펀딩의 갯수
    public int selectCountOfCF(Long memberId);

    //    크라우드 펀딩 리스트 출력 (페이지네이션)
    public List<CrowdFundingDTO> selectAll(Long memberId, Pagination pagination);

    //    크라우드 펀딩 객체 조회
    public Optional<CrowdFundingDTO> select(Long memberId);

}
