package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.crowdfunding.CrowdFundingDTO;
import com.app.onestepback.domain.model.CrowdFundingVO;
import com.app.onestepback.domain.type.crowdfunding.CrowdFundingStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CrowdFundingMapper {
    void insert(CrowdFundingVO vo);

    List<CrowdFundingDTO.ListView> selectListByArtistId(@Param("artistId") Long artistId);

    Optional<CrowdFundingDTO.Detail> selectById(@Param("id") Long id);

    Optional<CrowdFundingDTO.PaymentView> selectPaymentView(@Param("fundingId") Long fundingId, @Param("memberId") Long memberId);

    List<CrowdFundingVO> selectExpiredEndedFundings();

    List<CrowdFundingDTO.PublicList> selectFundingList(@Param("status") String status);

    void update(CrowdFundingVO vo);

    void updateStatus(Long fundingId, CrowdFundingStatus status);

    int updateExpiredFundings();
}
