package com.app.onestepback.mapper;

import com.app.onestepback.domain.BookmarkedArtistPostVO;
import com.app.onestepback.domain.BookmarkedVideoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface BookmarkedVideoMapper {
    public Optional<BookmarkedVideoVO> select(BookmarkedVideoVO bookmarkedVideoVO);
    public void insert(BookmarkedVideoVO bookmarkedVideoVO);
    public void delete(BookmarkedVideoVO bookmarkedVideoVO);
}
