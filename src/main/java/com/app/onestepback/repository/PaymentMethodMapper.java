package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.payment.PaymentMethodDTO;
import com.app.onestepback.domain.model.PaymentMethodVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PaymentMethodMapper {
    void insert(PaymentMethodVO vo);

    Optional<PaymentMethodVO> selectById(@Param("id") Long paymentId);

    List<PaymentMethodDTO> selectAllByMemberId(Long memberId);

    void updateAllDefaultToN(Long memberId);

    int updateDefaultToY(@Param("id") Long paymentId, @Param("memberId") Long memberId);

    void deleteById(@Param("id") Long paymentId);
}