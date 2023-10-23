package com.app.onestepback.service;

import com.app.onestepback.domain.CrowdFundingDTO;
import com.app.onestepback.domain.CrowdFundingVO;
import com.app.onestepback.domain.Pagination;

import java.util.List;
import java.util.Optional;

public interface ArtistCrowdFundingService {
    //    멤버아이디로 조회된 크라우드 펀딩의 갯수
    public int getCountOfCF(Long memberId);

    //    크라우드 펀딩 리스트 출력 (페이지네이션)
    public List<CrowdFundingDTO> getAllCrowdFundings(Long memberId, Pagination pagination);

    //    크라우드 펀딩 객체 조회
    public Optional<CrowdFundingDTO> getCrowdFunding(Long memberId);

}
