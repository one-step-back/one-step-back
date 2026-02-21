package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.feed.FeedDetailDTO;
import com.app.onestepback.domain.dto.feed.FeedFileDTO;
import com.app.onestepback.domain.dto.feed.FeedListDTO;
import com.app.onestepback.domain.dto.feed.FeedSearchCond;
import com.app.onestepback.domain.model.FeedVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FeedMapper {
    void insertFeed(FeedVO feedVO);

    List<FeedListDTO> selectFeedList(FeedSearchCond cond);

    Optional<FeedDetailDTO> selectFeedDetail(
            @Param("id") Long feedId,
            @Param("artistId") Long artistId,
            @Param("viewerId") Long viewerId
    );

    List<FeedFileDTO> selectFeedFiles(long feedId);

    void updateViewCount(long id);

    void updateFeed(FeedVO feedVO);

    int softDeleteByFeedIdAndArtistId(@Param("feedId") long feedId, @Param("artistId") Long artistId);

    int deleteById(long feedId);
}