package com.app.onestepback.repository;

import com.app.onestepback.domain.model.PaymentHistoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentHistoryMapper {
    void insert(PaymentHistoryVO vo);

    int insertFundingIncomeBulk(@Param("fundingId") Long fundingId, @Param("artistId") Long artistId);

    int updateSettlementId(@Param("artistId") Long artistId, @Param("settlementId") Long settlementId);

    void updateSettlementIdToNull(Long settlementId);
}
