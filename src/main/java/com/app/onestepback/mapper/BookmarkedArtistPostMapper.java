package com.app.onestepback.mapper;

import com.app.onestepback.domain.vo.BookmarkedArtistPostVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface BookmarkedArtistPostMapper {
    Optional<BookmarkedArtistPostVO> select(BookmarkedArtistPostVO bookmarkedArtistPostVO);

    void insert(Long postId, Long memberId);

    void delete(Long postId, Long memberId);
}
