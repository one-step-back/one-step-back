package com.app.onestepback.repository;

import com.app.onestepback.domain.BookmarkedArtistPostVO;
import com.app.onestepback.domain.BookmarkedPostVO;
import com.app.onestepback.domain.BookmarkedVideoVO;
import com.app.onestepback.mapper.BookmarkedArtistPostMapper;
import com.app.onestepback.mapper.BookmarkedVideoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookmarkedVideoDAO {
    private final BookmarkedVideoMapper bookmarkedVideoMapper;

    public Optional<BookmarkedVideoVO> getVideoBookmarkInfo(BookmarkedVideoVO bookmarkedVideoVO){return bookmarkedVideoMapper.select(bookmarkedVideoVO);}

    public void doBookmarkVideo(BookmarkedVideoVO bookmarkedVideoVO){
        bookmarkedVideoMapper.insert(bookmarkedVideoVO);
    }

    public void eraseBookmarkedVideo(BookmarkedVideoVO bookmarkedVideoVO){
        bookmarkedVideoMapper.delete(bookmarkedVideoVO);
    }
}
