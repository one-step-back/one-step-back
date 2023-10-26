package com.app.onestepback.mapper;

import com.app.onestepback.domain.SubscriptionDTO;
import com.app.onestepback.domain.SubscriptionVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SubscriptionMapper {
    public int selectCountOfSubscriber(Long artistId);

    public Optional<SubscriptionVO> select(SubscriptionVO subscriptionVO);

    public void insert(SubscriptionVO subscriptionVO);

    public void delete(SubscriptionVO subscriptionVO);

    public List<SubscriptionDTO> selectAll(Long memberId);
}
