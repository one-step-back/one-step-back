package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.crowdfunding.CrowdFundingDTO;
import com.app.onestepback.domain.dto.payment.FundingPaymentDTO;
import com.app.onestepback.domain.model.FundingPaymentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FundingPaymentMapper {
    void insert(FundingPaymentVO vo);

    Optional<CrowdFundingDTO.Receipt> selectReceipt(
            @Param("paymentId") Long paymentId,
            @Param("fundingId") Long fundingId,
            @Param("memberId") Long memberId
    );

    List<FundingPaymentDTO.History> selectFundingPaymentHistory(
            @Param("memberId") Long memberId,
            @Param("lastPaymentId") Long lastPaymentId,
            @Param("size") int size
    );


    List<FundingPaymentVO> selectPaidPaymentsByFundingId(Long fundingId);

    void updatePaymentsToRefunded(Long fundingId);
}