package com.app.onestepback.mapper;

import com.app.onestepback.domain.CombinedPostDTO;
import com.app.onestepback.domain.SubscriptionDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CombinedPostMapper {
    public List<CombinedPostDTO> select5Posts();

    public List<CombinedPostDTO> select4Posts();

    public Optional<CombinedPostDTO> selectLatestOne(Long memberId);
}
