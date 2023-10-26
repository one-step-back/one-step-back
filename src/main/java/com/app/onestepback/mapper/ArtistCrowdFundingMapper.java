package com.app.onestepback.mapper;

import com.app.onestepback.domain.*;
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

    //    멤버아이디로 조회된 펀딩 요청의 갯수
    public int selectCountOfFR(Long memberId);

    //    펀딩 요청 리스트 출력 (페이지네이션)
    public List<FundingRequestDTO> selectAllRequests(Long memberId, Pagination pagination);

    //    펀딩 요청 객체 조회
    public Optional<FundingRequestDTO> selectRequest(Long memberId);

    //    크라우드 펀딩 요청 작성
    public void insertRequest(FundingRequestVO fundingRequestVO);

    //    크라우드 펀딩 수락
    public void insert(CrowdFundingVO crowdFundingVO);

    //    크라우드 펀딩 수락
    public void updateToAccepted(Long id);

    //    크라우드 펀딩 거절
    public void updateToRejected(Long id);

    //    크라우드 펀딩 기간 만료
    public void updateToEnded(Long id);

    //    메인 페이지    
    public List<CrowdFundingDTO> select4Randomly();

}
