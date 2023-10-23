package com.app.onestepback.service;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.domain.CrowdFundingDTO;
import com.app.onestepback.domain.CrowdFundingVO;
import com.app.onestepback.domain.Pagination;
import com.app.onestepback.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ArtistCrowdFundingServiceImpl implements ArtistCrowdFundingService {
    private final ArtistDAO artistDAO;
    private final ArtistCrowdFundingDAO artistCrowdFundingDAO;

    //    아티스트 정보 가져오기
    public Optional<ArtistDTO> getArtist(Long id) {
        return artistDAO.getArtist(id);
    }

    //    멤버아이디로 조회된 크라우드 펀딩의 갯수
    @Override
    public int getCountOfCF(Long memberId) {
        return artistCrowdFundingDAO.getCountOfCF(memberId);
    }

    //    크라우드 펀딩 리스트 출력 (페이지네이션)
    @Override
    public List<CrowdFundingDTO> getAllCrowdFundings(Long memberId, Pagination pagination) {
        return artistCrowdFundingDAO.getAllCrowdFundings(memberId, pagination);
    }

    //    크라우드 펀딩 객체 조회
    @Override
    public Optional<CrowdFundingDTO> getCrowdFunding(Long memberId) {
        return artistCrowdFundingDAO.getCrowdFunding(memberId);
    }
}
