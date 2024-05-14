package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.SubscriptionDTO;
import com.app.onestepback.domain.vo.SubscriptionVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SubscriptionMapper {
    int selectCountOfSubscriber(Long artistId);

    Optional<SubscriptionVO> select(SubscriptionVO subscriptionVO);

    void insert(Long artistId, Long memberId);

    void delete(Long artistId, Long memberId);

    public List<SubscriptionDTO> selectAll(Long memberId);
}
