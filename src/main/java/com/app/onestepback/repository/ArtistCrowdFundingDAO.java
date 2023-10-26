package com.app.onestepback.repository;

import com.app.onestepback.domain.*;
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
    public int getCountOfCF(Long memberId) {
        return artistCrowdFundingMapper.selectCountOfCF(memberId);
    }

    //    크라우드 펀딩 리스트 출력 (페이지네이션)
    public List<CrowdFundingDTO> getAllCrowdFundings(Long memberId, Pagination pagination) {
        return artistCrowdFundingMapper.selectAll(memberId, pagination);
    }

    //    크라우드 펀딩 객체 조회
    public Optional<CrowdFundingDTO> getCrowdFunding(Long id) {
        return artistCrowdFundingMapper.select(id);
    }


    //    멤버아이디로 조회된 펀딩 요청의 갯수
    public int getCountOfFR(Long memberId){
        return artistCrowdFundingMapper.selectCountOfFR(memberId);
    }

    //    펀딩 요청 리스트 출력 (페이지네이션)
    public List<FundingRequestDTO> getAllRequests(Long memberId, Pagination pagination){
        return artistCrowdFundingMapper.selectAllRequests(memberId, pagination);
    }

    //    펀딩 요청 객체 조회
    public Optional<FundingRequestDTO> getRequest(Long memberId){
        return artistCrowdFundingMapper.selectRequest(memberId);
    }

    //    크라우드 펀딩 요청 작성
    public void requestFunding(FundingRequestVO fundingRequestVO){
        artistCrowdFundingMapper.insertRequest(fundingRequestVO);
    }

    //    크라우드 펀딩 수락
    public void acceptCrowdFundingRequest(CrowdFundingVO crowdFundingVO){
        artistCrowdFundingMapper.insert(crowdFundingVO);
    }

    //    크라우드 펀딩 수락
    public void acceptFundingRequest(Long id){
        artistCrowdFundingMapper.updateToAccepted(id);
    }

    //    크라우드 펀딩 거절
    public void rejectFundingRequest(Long id){
        artistCrowdFundingMapper.updateToRejected(id);
    }

    //    크라우드 펀딩 기간 만료
    public void endCrowdFunding(Long id){
        artistCrowdFundingMapper.updateToEnded(id);

    //    매인페이지 출력용
    public List<CrowdFundingDTO> get4FundingsRandomly() {
        return artistCrowdFundingMapper.select4Randomly();
    }
}
