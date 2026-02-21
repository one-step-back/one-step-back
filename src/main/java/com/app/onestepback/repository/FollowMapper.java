package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.follow.FollowDTO;
import com.app.onestepback.domain.dto.follow.FollowSearchCond;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FollowMapper {
    void insert(long artistId, long memberId);

    void delete(long artistId, long memberId);

    boolean exists(long artistId, long memberId);

    List<Long> findFollowerIdsByArtistId(long artistId);

    List<FollowDTO.Following> selectFollowingByMemberId(FollowSearchCond cond);

    List<FollowDTO.Follower> selectFollowersByArtistId(Long artistId);
}
