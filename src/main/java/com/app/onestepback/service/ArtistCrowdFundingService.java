package com.app.onestepback.service;

import com.app.onestepback.domain.*;

import java.util.List;
import java.util.Optional;

public interface ArtistCrowdFundingService {
    //    멤버아이디로 조회된 크라우드 펀딩의 갯수
    public int getCountOfCF(Long memberId);

    //    크라우드 펀딩 리스트 출력 (페이지네이션)
    public List<CrowdFundingDTO> getAllCrowdFundings(Long memberId, Pagination pagination);

    //    크라우드 펀딩 객체 조회
    public Optional<CrowdFundingDTO> getCrowdFunding(Long memberId);

    //    멤버아이디로 조회된 펀딩 요청의 갯수
    public int getCountOfFR(Long memberId);

    //    펀딩 요청 리스트 출력 (페이지네이션)
    public List<FundingRequestDTO> getAllRequests(Long memberId, Pagination pagination);

    //    펀딩 요청 객체 조회
    public Optional<FundingRequestDTO> getRequest(Long memberId);

    //    크라우드 펀딩 요청 작성
    public void requestFunding(FundingRequestVO fundingRequestVO);

    //    크라우드 펀딩 수락
    public void acceptCrowdFundingRequest(CrowdFundingVO crowdFundingVO);

    //    크라우드 펀딩 수락
    public void acceptFundingRequest(Long id);

    //    크라우드 펀딩 거절
    public void rejectFundingRequest(Long id);

    //    크라우드 펀딩 기간 만료
    public void endCrowdFunding(Long id);
}
