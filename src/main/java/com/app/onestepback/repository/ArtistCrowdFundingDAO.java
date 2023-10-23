package com.app.onestepback.repository;

import com.app.onestepback.domain.CrowdFundingDTO;
import com.app.onestepback.domain.CrowdFundingVO;
import com.app.onestepback.domain.Pagination;
import com.app.onestepback.mapper.ArtistCrowdFundingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArtistCrowdFundingDAO {
    private final ArtistCrowdFundingMapper artistCrowdFundingMapper;

    //    멤버아이디로 조회된 크라우드 펀딩의 갯수
    public int getCountOfCF(Long memberId){
        return artistCrowdFundingMapper.selectCountOfCF(memberId);
    }

    //    크라우드 펀딩 리스트 출력 (페이지네이션)
    public List<CrowdFundingDTO> getAllCrowdFundings(Long memberId, Pagination pagination){
        return artistCrowdFundingMapper.selectAll(memberId, pagination);
    }

    //    크라우드 펀딩 객체 조회
    public Optional<CrowdFundingDTO> getCrowdFunding(Long id){
        return artistCrowdFundingMapper.select(id);
    }

}
